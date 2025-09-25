package com.smarterp.order.bus;

import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoOrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;


@Component
public class MercadoCallBackMarketPlaceOrdersListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoCallBackMarketPlaceOrdersListener.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private RedisService redisService;

    /**
     * 美客多回调之订单
     *
     * @param jsonObject
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.MERCADO_CALL_BACK_INFO_MARKETPLACE_ORDERS_QUEUE),
            key = RabbitMQConstant.RoutingKey.MERCADO_CALL_BACK_INFO_MARKETPLACE_ORDERS))
    public void mercadoCallBackMarketPlaceOrdersListener(JSONObject jsonObject) {
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("MERCADO_CALL_BACK_INFO_MARKETPLACE_ORDERS:" + jsonObject.getString("resource"));
        boolean b = lock.tryLock();
        try {
            if (b) {
                String resource = jsonObject.getString("resource");
                String merchantId = jsonObject.getString("user_id");
                if (resource != null && merchantId != null) {
                    String[] split = resource.split("/");
                    String orderId = split[split.length - 1];
                    // TODO查一下订单的父ID 和 订单所属站点ID
                    String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:" + merchantId);
                    if(accessToken!=null){
                        JSONObject orderInfo = MercadoHttpUtil.getMercadoInfoByOrderId(accessToken, orderId);
                        if(orderInfo!=null){
                            JSONObject seller = orderInfo.getJSONObject("seller");
                            if(seller!=null){
                                String userId = seller.getString("id");
                                MercadoOrderInfo mercadoOrderInfo =  new MercadoOrderInfo();
                                mercadoOrderInfo.setId(orderId);
                                mercadoOrderInfo.setMerchantId(merchantId);
                                mercadoOrderInfo.setUserId(userId);

                                amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                        RabbitMQConstant.RoutingKey.SYNC_MERCADO_ORDER_PAGE_INFO_NEXT_DETAIL, mercadoOrderInfo);
                            }
                        }
                    }
                }
            } else {
                logger.error("美客多回调之订单没有拿到锁 resource {}", jsonObject.toJSONString());
            }
        } catch (Exception e) {
            logger.error("美客多回调之订单异常 {}", e);
        } finally {
            long end = System.currentTimeMillis();
            logger.error("美客多回调之订单耗费时间 {} resource {}", (end - startTime), jsonObject.toJSONString());
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("美客多回调之订单释放锁异常 {} resource {}", e, jsonObject.toJSONString());
            }
        }
    }

}
