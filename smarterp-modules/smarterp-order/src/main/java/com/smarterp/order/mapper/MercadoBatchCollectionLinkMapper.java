package com.smarterp.order.mapper;

import com.smarterp.order.domain.MercadoBatchCollectionLink;
import com.smarterp.order.domain.dto.MercadoBatchCollectionLinkDetails;
import com.smarterp.order.domain.dto.MercadoBatchCollectionLinkQuery;
import com.smarterp.order.domain.dto.MercadoUpcItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 批量采集的链接Mapper接口
 *
 * @author smarterp
 * @date 2024-05-27
 */
public interface MercadoBatchCollectionLinkMapper {
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
    public List<MercadoBatchCollectionLinkDetails> selectMercadoBatchCollectionLinkList(MercadoBatchCollectionLinkQuery query);

    /**
     * 新增批量采集的链接
     *
     * @param mercadoBatchCollectionLink 批量采集的链接
     * @return 结果
     */
    public int insertMercadoBatchCollectionLink(MercadoBatchCollectionLink mercadoBatchCollectionLink);

    /**
     * 修改批量采集的链接
     *
     * @param mercadoBatchCollectionLink 批量采集的链接
     * @return 结果
     */
    public int updateMercadoBatchCollectionLink(MercadoBatchCollectionLink mercadoBatchCollectionLink);

    /**
     * 删除批量采集的链接
     *
     * @param id 批量采集的链接主键
     * @return 结果
     */
    public int deleteMercadoBatchCollectionLinkById(String id);

    /**
     * 批量删除批量采集的链接
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoBatchCollectionLinkByIds(String[] ids);

    /**
     * 根据部门id获取足够数量的UPC编码
     *
     * @param deptId
     * @param count
     * @return
     */
    public List<MercadoUpcItem> getUpcCodeByDept(@Param("deptId") Long deptId, @Param("count") Integer count);

    /**
     * 更新UPC编码的状态，将状态从未使用更改为已使用
     * @param upcIdList
     * @return
     */
    public Integer updateUpcStatus(@Param("upcIdList")List<Long> upcIdList);

    /**
     * 更新upc编码的可用数量
     * @param upcId
     * @return
     */
    public Integer updateUpcCount(Long upcId);

    /**
     * 根据条件查询数量
     * @param example
     * @return
     */
    public Integer linkCount(MercadoBatchCollectionLink example);
}
