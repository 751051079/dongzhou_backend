package com.smarterp.order.service.impl;

import com.smarterp.order.domain.MercadoOrderInfoPayment;
import com.smarterp.order.mapper.MercadoOrderInfoPaymentMapper;
import com.smarterp.order.service.IMercadoOrderInfoPaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单支付信息Service业务层处理
 *
 * @author smarterp
 * @date 2024-06-15
 */
@Service
public class MercadoOrderInfoPaymentServiceImpl implements IMercadoOrderInfoPaymentService {

    @Resource
    private MercadoOrderInfoPaymentMapper mercadoOrderInfoPaymentMapper;

    /**
     * 查询订单支付信息
     *
     * @param id 订单支付信息主键
     * @return 订单支付信息
     */
    @Override
    public MercadoOrderInfoPayment selectMercadoOrderInfoPaymentById(String id) {
        return mercadoOrderInfoPaymentMapper.selectMercadoOrderInfoPaymentById(id);
    }

    /**
     * 查询订单支付信息列表
     *
     * @param mercadoOrderInfoPayment 订单支付信息
     * @return 订单支付信息
     */
    @Override
    public List<MercadoOrderInfoPayment> selectMercadoOrderInfoPaymentList(MercadoOrderInfoPayment mercadoOrderInfoPayment) {
        return mercadoOrderInfoPaymentMapper.selectMercadoOrderInfoPaymentList(mercadoOrderInfoPayment);
    }

    /**
     * 新增订单支付信息
     *
     * @param mercadoOrderInfoPayment 订单支付信息
     * @return 结果
     */
    @Override
    public int insertMercadoOrderInfoPayment(MercadoOrderInfoPayment mercadoOrderInfoPayment) {
        return mercadoOrderInfoPaymentMapper.insertMercadoOrderInfoPayment(mercadoOrderInfoPayment);
    }

    /**
     * 修改订单支付信息
     *
     * @param mercadoOrderInfoPayment 订单支付信息
     * @return 结果
     */
    @Override
    public int updateMercadoOrderInfoPayment(MercadoOrderInfoPayment mercadoOrderInfoPayment) {
        return mercadoOrderInfoPaymentMapper.updateMercadoOrderInfoPayment(mercadoOrderInfoPayment);
    }

    /**
     * 批量删除订单支付信息
     *
     * @param ids 需要删除的订单支付信息主键
     * @return 结果
     */
    @Override
    public int deleteMercadoOrderInfoPaymentByIds(String[] ids) {
        return mercadoOrderInfoPaymentMapper.deleteMercadoOrderInfoPaymentByIds(ids);
    }

    /**
     * 删除订单支付信息信息
     *
     * @param id 订单支付信息主键
     * @return 结果
     */
    @Override
    public int deleteMercadoOrderInfoPaymentById(String id) {
        return mercadoOrderInfoPaymentMapper.deleteMercadoOrderInfoPaymentById(id);
    }
}
