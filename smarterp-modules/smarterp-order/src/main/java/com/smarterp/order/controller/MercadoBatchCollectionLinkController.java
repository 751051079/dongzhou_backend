package com.smarterp.order.controller;

import com.smarterp.common.core.web.controller.BaseController;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.core.web.page.TableDataInfo;
import com.smarterp.common.log.annotation.Log;
import com.smarterp.common.log.enums.BusinessType;
import com.smarterp.common.security.annotation.RequiresPermissions;
import com.smarterp.order.domain.dto.*;
import com.smarterp.order.service.IMercadoBatchCollectionLinkService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 批量采集的链接Controller
 *
 * @author smarterp
 * @date 2024-05-27
 */
@RestController
@RequestMapping("/link")
public class MercadoBatchCollectionLinkController extends BaseController {

    @Resource
    private IMercadoBatchCollectionLinkService mercadoBatchCollectionLinkService;

    /**
     * 批量采集链接
     */
    @RequiresPermissions("link:batch:collection")
    @PostMapping("/batch/collection")
    public AjaxResult batchCollection(@RequestBody MercadoBatchCollectionLinkDTO mercadoBatchCollectionLinkDTO) {
        return mercadoBatchCollectionLinkService.batchCollection(mercadoBatchCollectionLinkDTO);
    }

    /**
     * 批量采集链接到店铺
     */
    @RequiresPermissions("link:batch:collection:to:shop")
    @PostMapping("/batch/collection/toShop")
    public AjaxResult batchCollectionToShop(@RequestBody BatchCollectionLinkToShop batchCollectionLinkToShop) {
        return mercadoBatchCollectionLinkService.batchCollectionToShop(batchCollectionLinkToShop);
    }

    /**
     * 查询批量采集的链接列表
     */
    @RequiresPermissions("system:link:list")
    @GetMapping("/list")
    public TableDataInfo list(MercadoBatchCollectionLinkQuery query, Integer pageNum, Integer pageSize) {
        List<MercadoBatchCollectionLinkDetails> list = mercadoBatchCollectionLinkService.selectMercadoBatchCollectionLinkList(query, pageNum, pageSize);
        return getDataTable(list);
    }


    /**
     * 新增批量采集的链接
     */
    @RequiresPermissions("system:link:add")
    @Log(title = "批量采集的链接", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody MercadoBatchCollectionLinkAddDTO mercadoBatchCollectionLinkAddDTO) {
        return mercadoBatchCollectionLinkService.insertMercadoBatchCollectionLink(mercadoBatchCollectionLinkAddDTO);
    }

    /**
     * 删除批量采集的链接
     */
    @RequiresPermissions("system:link:remove")
    @Log(title = "批量采集的链接", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(mercadoBatchCollectionLinkService.deleteMercadoBatchCollectionLinkByIds(ids));
    }

    /**
     * 导入采集的链接
     * @param file
     * @return
     */
    @Log(title = "批量采集的链接", businessType = BusinessType.IMPORT)
    @RequiresPermissions("system:link:importData")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file) {
        return mercadoBatchCollectionLinkService.importData(file);
    }
}
