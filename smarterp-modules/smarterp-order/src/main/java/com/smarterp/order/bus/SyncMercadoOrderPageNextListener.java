package com.smarterp.order.bus;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoOrderInfo;
import com.smarterp.order.domain.dto.MercadoOrderPage;
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
public class SyncMercadoOrderPageNextListener {

    private static final Logger logger = LoggerFactory.getLogger(SyncMercadoOrderPageNextListener.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    /**
     * 处理订单数据
     *
     * @param mercadoOrderPage
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.SYNC_MERCADO_ORDER_PAGE_INFO_NEXT_QUEUE),
            key = RabbitMQConstant.RoutingKey.SYNC_MERCADO_ORDER_PAGE_INFO_NEXT))
    public void syncMercadoOrderPageNextListener(MercadoOrderPage mercadoOrderPage) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("SYNC_MERCADO_ORDER_PAGE_INFO_NEXT:" +
                mercadoOrderPage.getMerchantId() + "-" + mercadoOrderPage.getOffset() + "-" + mercadoOrderPage.getLimit());
        boolean b = lock.tryLock();
        try {
            if (b) {
                // 根据merchantId获取accessToken信息
                String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:" + mercadoOrderPage.getMerchantId());
                if (accessToken != null) {
                    // 根据accessToken获取订单信息
                    JSONObject jsonObject = MercadoHttpUtil.getMercadoOrderInfo(mercadoOrderPage.getOffset(), mercadoOrderPage.getLimit(), accessToken);
                    if (jsonObject != null) {
                        JSONArray results = jsonObject.getJSONArray("results");
                        if (results != null && results.size() > 0) {
                            for (int i = 0; i < results.size(); i++) {
                                JSONObject object = results.getJSONObject(i);
                                JSONArray orders = object.getJSONArray("orders");
                                if (orders != null && orders.size() > 0) {
                                    for (int j = 0; j < orders.size(); j++) {
                                        JSONObject order = orders.getJSONObject(j);
                                        MercadoOrderInfo mercadoOrderInfo = new MercadoOrderInfo();
                                        mercadoOrderInfo.setId(order.getString("id"));
                                        mercadoOrderInfo.setParentId(object.getString("id"));
                                        mercadoOrderInfo.setMerchantId(mercadoOrderPage.getMerchantId());
                                        mercadoOrderInfo.setUserId(order.getJSONObject("seller").getString("id"));
                                        amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                                RabbitMQConstant.RoutingKey.SYNC_MERCADO_ORDER_PAGE_INFO_NEXT_DETAIL, mercadoOrderInfo);

                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                logger.error("处理订单数据没有拿到锁  merchantId {} offset {} limit {}", mercadoOrderPage.getMerchantId(), mercadoOrderPage.getOffset(), mercadoOrderPage.getLimit());
            }
        } catch (Exception e) {
            logger.error("处理订单数据异常 {} merchantId {} offset {} limit {}", e, mercadoOrderPage.getMerchantId(), mercadoOrderPage.getOffset(), mercadoOrderPage.getLimit());
        } finally {
            long end = System.currentTimeMillis();
            logger.error("处理订单数据耗费时间 {} merchantId {} offset {} limit {}", (end - startTime), mercadoOrderPage.getMerchantId(), mercadoOrderPage.getOffset(), mercadoOrderPage.getLimit());
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("处理订单数据释放锁异常 {} merchantId {} offset {} limit {}", e, mercadoOrderPage.getMerchantId(), mercadoOrderPage.getOffset(), mercadoOrderPage.getLimit());
            }
        }
    }

}
