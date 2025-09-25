package com.smarterp.order.bus;

import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.order.constant.RabbitMQConstant;
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
public class SyncMercadoOrderPageListener {

    private static final Logger logger = LoggerFactory.getLogger(SyncMercadoOrderPageListener.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    /**
     * 处理订单数据分页信息
     * 目前逻辑只同步两个月内的订单
     *
     * @param merchantId
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.SYNC_MERCADO_ORDER_PAGE_INFO_QUEUE),
            key = RabbitMQConstant.RoutingKey.SYNC_MERCADO_ORDER_PAGE_INFO))
    public void syncMercadoOrderPageListener(String merchantId) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("SYNC_MERCADO_ORDER_PAGE_INFO:" + merchantId);
        boolean b = lock.tryLock();
        try {
            if (b) {
                // 根据merchantId获取accessToken
                String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:" + merchantId);
                logger.info("获取到的accessToken {}", accessToken);
                if (accessToken != null) {
                    // 根据accessToken获取订单信息
                    JSONObject jsonObject = MercadoHttpUtil.getMercadoOrderInfo(0, 50, accessToken);
                    if (jsonObject != null) {
                        JSONObject paging = jsonObject.getJSONObject("paging");
                        if (paging != null) {
                            Integer total = paging.getInteger("total");
                            if (total > 0) {
                                Integer totalPage = 0;
                                if (total % 50 == 0) {
                                    totalPage = total / 50;
                                } else {
                                    totalPage = total / 50 + 1;
                                }
                                for (int index = 0; index < totalPage; index++) {
                                    Integer offset = index * 50;
                                    Integer limit = 50;
                                    MercadoOrderPage mercadoOrderPage = new MercadoOrderPage();
                                    mercadoOrderPage.setOffset(offset);
                                    mercadoOrderPage.setLimit(limit);
                                    mercadoOrderPage.setMerchantId(merchantId);
                                    amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                            RabbitMQConstant.RoutingKey.SYNC_MERCADO_ORDER_PAGE_INFO_NEXT, mercadoOrderPage);
                                }
                            }
                        }
                    }
                }
            } else {
                logger.error("处理订单数据分页信息没有获取到分布式锁 merchantId {}", merchantId);
            }
        } catch (Exception e) {
            logger.error("处理店铺数据分页信息异常 {} merchantId {}", e, merchantId);
        } finally {
            long end = System.currentTimeMillis();
            logger.error("处理订单数据分页信息耗费时间 {} merchantId {}", (end - startTime), merchantId);
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("处理店铺数据分页信息释放锁异常 {} merchantId {}", e, merchantId);
            }
        }
    }

}
