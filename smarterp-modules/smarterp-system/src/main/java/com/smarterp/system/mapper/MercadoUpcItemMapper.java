package com.smarterp.system.mapper;

import java.util.List;

import com.smarterp.system.domain.MercadoUpcItem;

/**
 * UPC信息详情Mapper接口
 *
 * @author smarterp
 * @date 2023-04-16
 */
public interface MercadoUpcItemMapper {
    /**
     * 查询UPC信息详情
     *
     * @param id UPC信息详情主键
     * @return UPC信息详情
     */
    public MercadoUpcItem selectMercadoUpcItemById(Long id);

    /**
     * 查询UPC信息详情列表
     *
     * @param mercadoUpcItem UPC信息详情
     * @return UPC信息详情集合
     */
    public List<MercadoUpcItem> selectMercadoUpcItemList(MercadoUpcItem mercadoUpcItem);

    /**
     * 新增UPC信息详情
     *
     * @param mercadoUpcItem UPC信息详情
     * @return 结果
     */
    public int insertMercadoUpcItem(MercadoUpcItem mercadoUpcItem);

    /**
     * 修改UPC信息详情
     *
     * @param mercadoUpcItem UPC信息详情
     * @return 结果
     */
    public int updateMercadoUpcItem(MercadoUpcItem mercadoUpcItem);

    /**
     * 删除UPC信息详情
     *
     * @param id UPC信息详情主键
     * @return 结果
     */
    public int deleteMercadoUpcItemById(Long id);

    /**
     * 批量删除UPC信息详情
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoUpcItemByIds(Long[] ids);

    /**
     * 根据upcId删除upc详情
     *
     * @param upcId
     * @return
     */
    Integer deleteMercadoUpcItemByUpcId(Long upcId);
}
