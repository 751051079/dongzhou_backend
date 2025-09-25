package com.smarterp.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.common.security.utils.SecurityUtils;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoOrderInfo;
import com.smarterp.order.domain.dto.MercadoOrderInfoDetail;
import com.smarterp.order.mapper.MercadoOrderInfoMapper;
import com.smarterp.order.service.IMercadoOrderInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单信息Service业务层处理
 *
 * @author smarterp
 * @date 2024-06-07
 */
@Service
public class MercadoOrderInfoServiceImpl implements IMercadoOrderInfoService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoOrderInfoServiceImpl.class);

    @Resource
    private MercadoOrderInfoMapper mercadoOrderInfoMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisService redisService;

    /**
     * 查询订单信息
     *
     * @param id 订单信息主键
     * @return 订单信息
     */
    @Override
    public MercadoOrderInfo selectMercadoOrderInfoById(String id) {
        return mercadoOrderInfoMapper.selectMercadoOrderInfoById(id);
    }

    /**
     * 查询订单信息列表
     *
     * @param mercadoOrderInfo 订单信息
     * @return 订单信息
     */
    @Override
    public List<MercadoOrderInfoDetail> selectMercadoOrderInfoList(MercadoOrderInfo mercadoOrderInfo, Integer pageNum, Integer pageSize) {
        // 增加权限，每个账号只能看本部门的数据
        String userName = SecurityUtils.getLoginUser().getSysUser().getUserName();
        if(!"admin".equals(userName)){
            // 根据deptId查询到所有的merchantId
            Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
            List<String> merchantIds = mercadoOrderInfoMapper.getMerchantIdsByDeptId(deptId);
            if(merchantIds.isEmpty()){
                merchantIds.add("666666");
            }
            mercadoOrderInfo.setMerchantIds(merchantIds);
        }
        PageHelper.startPage(pageNum, pageSize);
        if (mercadoOrderInfo.getCreateBeginTime() != null && mercadoOrderInfo.getCreateEndTime() != null) {
            mercadoOrderInfo.setCreateBeginTime(mercadoOrderInfo.getCreateBeginTime() + " 00:00:00");
            mercadoOrderInfo.setCreateEndTime(mercadoOrderInfo.getCreateEndTime() + " 23:59:59");
        }
        List<MercadoOrderInfoDetail> mercadoOrderInfoDetails = mercadoOrderInfoMapper.selectMercadoOrderInfoList(mercadoOrderInfo);
        if (!mercadoOrderInfoDetails.isEmpty()) {
            for (MercadoOrderInfoDetail mercadoOrderInfoDetail : mercadoOrderInfoDetails) {
                if (mercadoOrderInfoDetail.getMercadoShopId() != null) {
                    String mercadoShopName = redisService.getCacheObject("MERCADO_SHOP_INFO:NAME:" + mercadoOrderInfoDetail.getMercadoShopId());
                    if (mercadoShopName != null) {
                        mercadoOrderInfoDetail.setMercadoShopName(mercadoShopName);
                    }
                }
                try {
                    String variationAttributes = mercadoOrderInfoDetail.getVariationAttributes();
                    if (variationAttributes != null) {
                        JSONArray jsonArray = JSON.parseArray(variationAttributes);
                        if (jsonArray != null && jsonArray.size() > 0) {
                            List<String> orderSkuInfoList = new ArrayList<>();
                            for (int index = 0; index < jsonArray.size(); index++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(index);
                                String name = jsonObject.getString("name");
                                String valueName = jsonObject.getString("value_name");
                                String skuInfo = name + " : " + valueName;
                                orderSkuInfoList.add(skuInfo);
                            }
                            mercadoOrderInfoDetail.setOrderSkuInfo(orderSkuInfoList);
                        }
                    }
                } catch (Exception e) {
                    logger.error("处理变体数据异常 {}", e);
                }
            }
        }
        return mercadoOrderInfoDetails;
    }

    /**
     * 新增订单信息
     *
     * @param mercadoOrderInfo 订单信息
     * @return 结果
     */
    @Override
    public int insertMercadoOrderInfo(MercadoOrderInfo mercadoOrderInfo) {
        return mercadoOrderInfoMapper.insertMercadoOrderInfo(mercadoOrderInfo);
    }

    /**
     * 修改订单信息
     *
     * @param mercadoOrderInfo 订单信息
     * @return 结果
     */
    @Override
    public int updateMercadoOrderInfo(MercadoOrderInfo mercadoOrderInfo) {
        return mercadoOrderInfoMapper.updateMercadoOrderInfo(mercadoOrderInfo);
    }

    /**
     * 批量删除订单信息
     *
     * @param ids 需要删除的订单信息主键
     * @return 结果
     */
    @Override
    public int deleteMercadoOrderInfoByIds(String[] ids) {
        return mercadoOrderInfoMapper.deleteMercadoOrderInfoByIds(ids);
    }

    /**
     * 删除订单信息信息
     *
     * @param id 订单信息主键
     * @return 结果
     */
    @Override
    public int deleteMercadoOrderInfoById(String id) {
        return mercadoOrderInfoMapper.deleteMercadoOrderInfoById(id);
    }

    /**
     * 同步订单数据
     *
     * @param merchantId
     * @return
     */
    @Override
    public AjaxResult syncShopOrderInfo(String merchantId) {
        amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                RabbitMQConstant.RoutingKey.SYNC_MERCADO_ORDER_PAGE_INFO, merchantId);
        return AjaxResult.success("同步订单成功,请稍后刷新页面!");
    }

    @Override
    public int updateOrderInfo(String[] ids) {
        for (int index = 0; index < ids.length; index++) {
            String orderId = ids[index];
            MercadoOrderInfo mercadoOrderInfo = mercadoOrderInfoMapper.selectMercadoOrderInfoById(orderId);
            if (mercadoOrderInfo != null) {
                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                        RabbitMQConstant.RoutingKey.SYNC_MERCADO_ORDER_PAGE_INFO_NEXT_DETAIL, mercadoOrderInfo);
            }
        }
        return ids.length;
    }
}
