package com.smarterp.order.service.impl;

import com.smarterp.order.domain.MercadoOrderInfoItem;
import com.smarterp.order.mapper.MercadoOrderInfoItemMapper;
import com.smarterp.order.service.IMercadoOrderInfoItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单产品Service业务层处理
 *
 * @author smarterp
 * @date 2024-06-07
 */
@Service
public class MercadoOrderInfoItemServiceImpl implements IMercadoOrderInfoItemService {

    @Resource
    private MercadoOrderInfoItemMapper mercadoOrderInfoItemMapper;

    /**
     * 查询订单产品
     *
     * @param id 订单产品主键
     * @return 订单产品
     */
    @Override
    public MercadoOrderInfoItem selectMercadoOrderInfoItemById(String id) {
        return mercadoOrderInfoItemMapper.selectMercadoOrderInfoItemById(id);
    }

    /**
     * 查询订单产品列表
     *
     * @param mercadoOrderInfoItem 订单产品
     * @return 订单产品
     */
    @Override
    public List<MercadoOrderInfoItem> selectMercadoOrderInfoItemList(MercadoOrderInfoItem mercadoOrderInfoItem) {
        return mercadoOrderInfoItemMapper.selectMercadoOrderInfoItemList(mercadoOrderInfoItem);
    }

    /**
     * 新增订单产品
     *
     * @param mercadoOrderInfoItem 订单产品
     * @return 结果
     */
    @Override
    public int insertMercadoOrderInfoItem(MercadoOrderInfoItem mercadoOrderInfoItem) {
        return mercadoOrderInfoItemMapper.insertMercadoOrderInfoItem(mercadoOrderInfoItem);
    }

    /**
     * 修改订单产品
     *
     * @param mercadoOrderInfoItem 订单产品
     * @return 结果
     */
    @Override
    public int updateMercadoOrderInfoItem(MercadoOrderInfoItem mercadoOrderInfoItem) {
        return mercadoOrderInfoItemMapper.updateMercadoOrderInfoItem(mercadoOrderInfoItem);
    }

    /**
     * 批量删除订单产品
     *
     * @param ids 需要删除的订单产品主键
     * @return 结果
     */
    @Override
    public int deleteMercadoOrderInfoItemByIds(String[] ids) {
        return mercadoOrderInfoItemMapper.deleteMercadoOrderInfoItemByIds(ids);
    }

    /**
     * 删除订单产品信息
     *
     * @param id 订单产品主键
     * @return 结果
     */
    @Override
    public int deleteMercadoOrderInfoItemById(String id) {
        return mercadoOrderInfoItemMapper.deleteMercadoOrderInfoItemById(id);
    }
}
