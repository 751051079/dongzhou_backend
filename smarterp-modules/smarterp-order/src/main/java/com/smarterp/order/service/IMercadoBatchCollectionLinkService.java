package com.smarterp.order.service;

import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.order.domain.MercadoBatchCollectionLink;
import com.smarterp.order.domain.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 批量采集的链接Service接口
 *
 * @author smarterp
 * @date 2024-05-27
 */
public interface IMercadoBatchCollectionLinkService {
    /**
     * 查询批量采集的链接
     *
     * @param id 批量采集的链接主键
     * @return 批量采集的链接
     */
    public MercadoBatchCollectionLink selectMercadoBatchCollectionLinkById(String id);

    /**
     * 查询批量采集的链接列表
     *
     * @param query 批量采集的链接
     * @return 批量采集的链接集合
     */
    public List<MercadoBatchCollectionLinkDetails> selectMercadoBatchCollectionLinkList(MercadoBatchCollectionLinkQuery query, Integer pageNum, Integer pageSize);

    /**
     * 新增批量采集的链接
     *
     * @param mercadoBatchCollectionLinkAddDTO 批量采集的链接
     * @return 结果
     */
    public AjaxResult insertMercadoBatchCollectionLink(MercadoBatchCollectionLinkAddDTO mercadoBatchCollectionLinkAddDTO);

    /**
     * 修改批量采集的链接
     *
     * @param mercadoBatchCollectionLink 批量采集的链接
     * @return 结果
     */
    public int updateMercadoBatchCollectionLink(MercadoBatchCollectionLink mercadoBatchCollectionLink);

    /**
     * 批量删除批量采集的链接
     *
     * @param ids 需要删除的批量采集的链接主键集合
     * @return 结果
     */
    public int deleteMercadoBatchCollectionLinkByIds(String[] ids);

    /**
     * 删除批量采集的链接信息
     *
     * @param id 批量采集的链接主键
     * @return 结果
     */
    public int deleteMercadoBatchCollectionLinkById(String id);

    /**
     * 批量采集链接
     *
     * @param mercadoBatchCollectionLinkDTO
     * @return
     */
    public AjaxResult batchCollection(MercadoBatchCollectionLinkDTO mercadoBatchCollectionLinkDTO);

    /**
     * 批量采集链接到店铺
     *
     * @param batchCollectionLinkToShop
     * @return
     */
    public AjaxResult batchCollectionToShop(BatchCollectionLinkToShop batchCollectionLinkToShop);

    /**
     * 导入链接
     * @param file
     * @return
     */
    public AjaxResult importData(MultipartFile file);
}
