package com.smarterp.order.service;

import java.util.List;

import com.smarterp.order.domain.MercadoOrderInfoItem;

/**
 * 订单产品Service接口
 *
 * @author smarterp
 * @date 2024-06-07
 */
public interface IMercadoOrderInfoItemService {
    /**
     * 查询订单产品
     *
     * @param id 订单产品主键
     * @return 订单产品
     */
    public MercadoOrderInfoItem selectMercadoOrderInfoItemById(String id);

    /**
     * 查询订单产品列表
     *
     * @param mercadoOrderInfoItem 订单产品
     * @return 订单产品集合
     */
    public List<MercadoOrderInfoItem> selectMercadoOrderInfoItemList(MercadoOrderInfoItem mercadoOrderInfoItem);

    /**
     * 新增订单产品
     *
     * @param mercadoOrderInfoItem 订单产品
     * @return 结果
     */
    public int insertMercadoOrderInfoItem(MercadoOrderInfoItem mercadoOrderInfoItem);

    /**
     * 修改订单产品
     *
     * @param mercadoOrderInfoItem 订单产品
     * @return 结果
     */
    public int updateMercadoOrderInfoItem(MercadoOrderInfoItem mercadoOrderInfoItem);

    /**
     * 批量删除订单产品
     *
     * @param ids 需要删除的订单产品主键集合
     * @return 结果
     */
    public int deleteMercadoOrderInfoItemByIds(String[] ids);

    /**
     * 删除订单产品信息
     *
     * @param id 订单产品主键
     * @return 结果
     */
    public int deleteMercadoOrderInfoItemById(String id);
}
