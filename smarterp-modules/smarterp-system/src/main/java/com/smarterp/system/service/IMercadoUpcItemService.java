package com.smarterp.system.service;

import java.util.List;

import com.smarterp.system.domain.MercadoUpcItem;

/**
 * UPC信息详情Service接口
 *
 * @author smarterp
 * @date 2023-04-16
 */
public interface IMercadoUpcItemService {
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
     * 批量删除UPC信息详情
     *
     * @param ids 需要删除的UPC信息详情主键集合
     * @return 结果
     */
    public int deleteMercadoUpcItemByIds(Long[] ids);

    /**
     * 删除UPC信息详情信息
     *
     * @param id UPC信息详情主键
     * @return 结果
     */
    public int deleteMercadoUpcItemById(Long id);
}
