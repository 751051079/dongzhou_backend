package com.smarterp.order.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.smarterp.common.core.domain.R;
import com.smarterp.common.core.utils.MultipartFIleUtil;
import com.smarterp.common.core.utils.StringUtils;
import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.order.domain.MercadoProduct;
import com.smarterp.order.domain.dto.BatchCopyLinkToShop;
import com.smarterp.order.domain.dto.MercadoInfoDto;
import com.smarterp.order.domain.vo.MercadoProductDetail;
import com.smarterp.order.domain.vo.MercadoReleaseProductDTO;
import com.smarterp.order.domain.vo.MercadoSizeChartDTO;
import com.smarterp.order.service.IMercadoProductService;
import com.smarterp.system.api.RemoteFileService;
import com.smarterp.system.api.domain.SysFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 产品Controller
 *
 * @author smarterp
 * @date 2023-04-26
 */
@RestController
@RequestMapping("/product")
public class MercadoProductController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MercadoProductController.class);

    @Resource
    private IMercadoProductService mercadoProductService;

    @Resource
    private RemoteFileService remoteFileService;

    @PostMapping("/getSizeChart")
    public AjaxResult getSizeChart(@RequestBody MercadoSizeChartDTO inputDto) {
        return mercadoProductService.getSizeChart(inputDto);
    }

    /**
     * 发布产品
     *
     * @param mercadoReleaseProductDTO
     * @return
     */
    @RequiresPermissions("order:product:release")
    @PostMapping("/release")
    public AjaxResult releaseProduct(@RequestBody MercadoReleaseProductDTO mercadoReleaseProductDTO) {
        logger.info("重新发布产品参数 {}", JSON.toJSONString(mercadoReleaseProductDTO));
        return mercadoProductService.releaseProduct(mercadoReleaseProductDTO);
    }

    /**
     * 修改产品
     */
    @RequiresPermissions("order:product:edit")
    @Log(title = "产品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MercadoInfoDto mercadoInfoDto) {
        return mercadoProductService.updateMercadoProduct(mercadoInfoDto);
    }

    /**
     * 删除产品
     */
    @RequiresPermissions("order:product:remove")
    @Log(title = "产品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return mercadoProductService.deleteMercadoProductById(id);
    }

    /**
     * 查询产品列表
     */
    @RequiresPermissions("order:product:list")
    @GetMapping("/list")
    public TableDataInfo list(MercadoProduct mercadoProduct) {
        startPage();
        List<MercadoProductDetail> list = mercadoProductService.selectMercadoProductList(mercadoProduct);
        return getDataTable(list);
    }

    /**
     * 获取产品详细信息
     */
    @RequiresPermissions("order:product:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(mercadoProductService.selectMercadoProductById(id));
    }

    /**
     * 批量删除产品
     *
     * @param ids
     * @return
     */
    @Log(title = "产品", businessType = BusinessType.DELETE)
    @DeleteMapping("/batchDetele/{ids}")
    public AjaxResult batchDelete(@PathVariable String[] ids) {
        for (int index = 0; index < ids.length; index++) {
            String id = ids[index];
            mercadoProductService.deleteMercadoProductById(Long.parseLong(id));
        }
        return AjaxResult.success("批量删除产品成功!");
    }

    /**
     * 批量发布产品
     *
     * @param ids
     * @return
     */
    @Log(title = "产品", businessType = BusinessType.GRANT)
    @GetMapping("/batchRelease/{ids}")
    public AjaxResult batchRelease(@PathVariable String[] ids) {
        // 首先将产品的发布状态更改为发布中
        Integer result = mercadoProductService.updateMercadoProductStatus(ids);
        if (result > 0) {
            for (int index = 0; index < ids.length; index++) {
                String productId = ids[index];
                mercadoProductService.releaseGlobalProduct(productId);
            }
        }
        logger.info("批量发布产品参数 {}", JSON.toJSONString(ids));
        return AjaxResult.success("批量发布产品成功,请稍后查询发布结果!");
    }

    /**
     * 新增产品
     */
    @RequiresPermissions("order:product:add")
    @Log(title = "产品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MercadoProduct mercadoProduct) {
        return toAjax(mercadoProductService.insertMercadoProduct(mercadoProduct));
    }


    @PostMapping("/uploadMercadoApi")
    public AjaxResult uploadMercadoApi(@RequestParam(name = "file") MultipartFile file) {
        return AjaxResult.success(mercadoProductService.uploadMercadoApi(file));
    }

    //通过url地址下载网络图片并且上传到文件系统
    @PostMapping("/uploadImgByUrl")
    public AjaxResult uploadImgByUrl(@RequestBody Map<String, String> urls) {
        return AjaxResult.success(mercadoProductService.uploadImgByUrl(urls));
    }


    /**
     * 获取产品详细信息
     */
    @GetMapping(value = "/getProductInfoById/{id}")
    public AjaxResult getProductInfoById(@PathVariable("id") Long id) {
        return success(mercadoProductService.getProductInfoById(id));
    }

    @PostMapping("/getMercadoInfo")
    public AjaxResult getMercadoInfo(@RequestParam String mercadoInfo) {

        if (StringUtils.isEmpty(mercadoInfo)) {
            return AjaxResult.error("采集链接不能为空");
        } else {
            return AjaxResult.success(mercadoProductService.getMercadoInfo(mercadoInfo));
        }
    }


    private void urlHandleImagesOld(String url, List<Map<String, Object>> list) {
        HttpRequest get = HttpUtil.createGet(url);
        String urlOne = null;
        try (InputStream inputStream = get.execute().bodyStream();) {
            R<SysFile> upload = remoteFileService.upload(new MultipartFIleUtil(UUID.randomUUID().toString() + ".jpg",
                    UUID.randomUUID().toString() + ".jpg", null, inputStream));
            logger.error("上传文件图片返回:{}", JSON.toJSONString(upload));
            if (upload.getCode() == 200) {
                urlOne = upload.getData().getUrl();
            }
        } catch (Exception e) {
            logger.error("上传文件失败url:{}", url);
        }
        if (null != urlOne) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", UUID.randomUUID());
            map.put("url", urlOne);
            list.add(map);
        }
    }

    /**
     * 获取错误日志
     */
    @GetMapping(value = "/getErrorLog/{id}")
    public AjaxResult getErrorLog(@PathVariable("id") Long id) {
        return mercadoProductService.getErrorLog(id);
    }

    /**
     * 批量复制链接到店铺
     */
    @PostMapping("/batch/copy/toShop")
    public AjaxResult batchCopyToShop(@RequestBody BatchCopyLinkToShop batchCopyLinkToShop) {
        return mercadoProductService.batchCopyToShop(batchCopyLinkToShop);
    }


}
