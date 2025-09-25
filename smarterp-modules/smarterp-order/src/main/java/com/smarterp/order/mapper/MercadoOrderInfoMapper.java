package com.smarterp.order.mapper;

import com.smarterp.order.domain.MercadoOrderInfo;
import com.smarterp.order.domain.dto.MercadoOrderInfoDetail;

import java.util.List;

/**
 * 订单信息Mapper接口
 *
 * @author smarterp
 * @date 2024-06-07
 */
public interface MercadoOrderInfoMapper {
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
    public List<MercadoOrderInfoDetail> selectMercadoOrderInfoList(MercadoOrderInfo mercadoOrderInfo);

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
     * 删除订单信息
     *
     * @param id 订单信息主键
     * @return 结果
     */
    public int deleteMercadoOrderInfoById(String id);

    /**
     * 批量删除订单信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoOrderInfoByIds(String[] ids);

    /**
     * 根据deptId查询到所有的merchantId
     * @param deptId
     * @return
     */
    public List<String> getMerchantIdsByDeptId(Long deptId);
}
