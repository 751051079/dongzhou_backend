package com.smarterp.order.service;

import java.util.List;

import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.order.domain.MercadoOrderInfo;
import com.smarterp.order.domain.dto.MercadoOrderInfoDetail;

/**
 * 订单信息Service接口
 *
 * @author smarterp
 * @date 2024-06-07
 */
public interface IMercadoOrderInfoService {
    /**
     * 查询订单信息
     *
     * @param id 订单信息主键
     * @return 订单信息
     */
    public MercadoOrderInfo selectMercadoOrderInfoById(String id);

    /**
     * 查询订单信息列表
     *
     * @param mercadoOrderInfo 订单信息
     * @return 订单信息集合
     */
    public List<MercadoOrderInfoDetail> selectMercadoOrderInfoList(MercadoOrderInfo mercadoOrderInfo, Integer pageNum, Integer pageSize);

    /**
     * 新增订单信息
     *
     * @param mercadoOrderInfo 订单信息
     * @return 结果
     */
    public int insertMercadoOrderInfo(MercadoOrderInfo mercadoOrderInfo);

    /**
     * 修改订单信息
     *
     * @param mercadoOrderInfo 订单信息
     * @return 结果
     */
    public int updateMercadoOrderInfo(MercadoOrderInfo mercadoOrderInfo);

    /**
     * 批量删除订单信息
     *
     * @param ids 需要删除的订单信息主键集合
     * @return 结果
     */
    public int deleteMercadoOrderInfoByIds(String[] ids);

    /**
     * 删除订单信息信息
     *
     * @param id 订单信息主键
     * @return 结果
     */
    public int deleteMercadoOrderInfoById(String id);

    /**
     * 同步店铺订单数据
     *
     * @param merchantId
     * @return
     */
    public AjaxResult syncShopOrderInfo(String merchantId);

    /**
     * 同步订单信息
     * @param ids
     * @return
     */
    public int updateOrderInfo(String[] ids);
}
