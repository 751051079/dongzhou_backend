package com.smarterp.order.service;

import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.order.domain.MercadoProduct;
import com.smarterp.order.domain.dto.BatchCopyLinkToShop;
import com.smarterp.order.domain.dto.CpMercadoInfoDto;
import com.smarterp.order.domain.dto.MercadoInfoDto;
import com.smarterp.order.domain.vo.MercadoProductDetail;
import com.smarterp.order.domain.vo.MercadoReleaseProductDTO;
import com.smarterp.order.domain.vo.MercadoSizeChartDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 产品Service接口
 *
 * @author smarterp
 * @date 2023-04-26
 */
public interface IMercadoProductService {
    /**
     * 查询产品
     *
     * @param id 产品主键
     * @return 产品
     */
    public MercadoProduct selectMercadoProductById(Long id);

    /**
     * 查询产品列表
     *
     * @param mercadoProduct 产品
     * @return 产品集合
     */
    public List<MercadoProductDetail> selectMercadoProductList(MercadoProduct mercadoProduct);

    /**
     * 新增产品
     *
     * @param mercadoProduct 产品
     * @return 结果
     */
    public int insertMercadoProduct(MercadoProduct mercadoProduct);

    /**
     * 修改产品
     *
     * @param mercadoInfoDto 产品
     * @return 结果
     */
    public AjaxResult updateMercadoProduct(MercadoInfoDto mercadoInfoDto);

    /**
     * 批量删除产品
     *
     * @param ids 需要删除的产品主键集合
     * @return 结果
     */
    public int deleteMercadoProductByIds(Long[] ids);

    /**
     * 删除产品信息
     *
     * @param id 产品主键
     * @return 结果
     */
    public AjaxResult deleteMercadoProductById(Long id);

    public CpMercadoInfoDto getMercadoInfo(String mercadoInfo);

    public Map<String, Object> uploadMercadoApi(MultipartFile file);

    public List<Map<String, Object>> uploadImgByUrl(Map<String, String> urls);

    /**
     * 重新发布产品
     *
     * @param mercadoReleaseProductDTO
     * @return
     */
    public AjaxResult releaseProduct(MercadoReleaseProductDTO mercadoReleaseProductDTO);

    public CpMercadoInfoDto getProductInfoById(Long id);

    /**
     * 获取尺码表
     * @param inputDto
     * @return
     */
    public AjaxResult getSizeChart(MercadoSizeChartDTO inputDto);

    /**
     * 发布全局产品
     * @param productId
     * @return
     */
    public Integer releaseGlobalProduct(String productId);

    /**
     * 获取错误日志
     * @param id
     * @return
     */
    public AjaxResult getErrorLog(Long id);

    /**
     * 批量复制链接到店铺
     * @param batchCopyLinkToShop
     * @return
     */
    public AjaxResult batchCopyToShop(BatchCopyLinkToShop batchCopyLinkToShop);

    /**
     * 根据产品id将状态更改为发布中
     * @param productIds
     * @return
     */
    public Integer updateMercadoProductStatus(String[] productIds);
}
