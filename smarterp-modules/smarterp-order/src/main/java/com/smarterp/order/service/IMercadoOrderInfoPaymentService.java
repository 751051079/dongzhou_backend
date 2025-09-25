package com.smarterp.order.service;

import java.util.List;

import com.smarterp.order.domain.MercadoOrderInfoPayment;

/**
 * 订单支付信息Service接口
 *
 * @author smarterp
 * @date 2024-06-15
 */
public interface IMercadoOrderInfoPaymentService {
    /**
     * 查询订单支付信息
     *
     * @param id 订单支付信息主键
     * @return 订单支付信息
     */
    public MercadoOrderInfoPayment selectMercadoOrderInfoPaymentById(String id);

    /**
     * 查询订单支付信息列表
     *
     * @param mercadoOrderInfoPayment 订单支付信息
     * @return 订单支付信息集合
     */
    public List<MercadoOrderInfoPayment> selectMercadoOrderInfoPaymentList(MercadoOrderInfoPayment mercadoOrderInfoPayment);

    /**
     * 新增订单支付信息
     *
     * @param mercadoOrderInfoPayment 订单支付信息
     * @return 结果
     */
    public int insertMercadoOrderInfoPayment(MercadoOrderInfoPayment mercadoOrderInfoPayment);

    /**
     * 修改订单支付信息
     *
     * @param mercadoOrderInfoPayment 订单支付信息
     * @return 结果
     */
    public int updateMercadoOrderInfoPayment(MercadoOrderInfoPayment mercadoOrderInfoPayment);

    /**
     * 批量删除订单支付信息
     *
     * @param ids 需要删除的订单支付信息主键集合
     * @return 结果
     */
    public int deleteMercadoOrderInfoPaymentByIds(String[] ids);

    /**
     * 删除订单支付信息信息
     *
     * @param id 订单支付信息主键
     * @return 结果
     */
    public int deleteMercadoOrderInfoPaymentById(String id);
}
