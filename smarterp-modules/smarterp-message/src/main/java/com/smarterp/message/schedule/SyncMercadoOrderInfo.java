package com.smarterp.message.schedule;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.message.constant.RabbitMQConstant;
import com.smarterp.message.domain.MercadoCallBack;
import com.smarterp.message.mapper.MercadoCallBackMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.locks.Lock;

@Component
public class SyncMercadoOrderInfo {

    private static final Logger logger = LoggerFactory.getLogger(SyncMercadoOrderInfo.class);

    @Resource
    private MercadoCallBackMapper mercadoCallBackMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private RedisService redisService;

    /**
     * 同步订单定时任务
     */
    @Scheduled(fixedDelay = 60 * 1000)
    public void syncMercadoOrderInfo() {
        logger.info("同步订单定时任务启动=============");
        Lock lock = redisLockRegistry.obtain("syncMercadoOrderInfo");
        boolean b = lock.tryLock();
        try {
            if (b) {
                MercadoCallBack example = new MercadoCallBack();
                example.setTopic("marketplace_orders");
                String limit = redisService.getCacheObject("sys_config:process.market.place.orders");
                example.setLimit(Integer.parseInt(limit));
                List<MercadoCallBack> mercadoCallBacks = mercadoCallBackMapper.selectMercadoCallBackListLimit(example);
                if (!mercadoCallBacks.isEmpty()) {
                    Long[] ids = new Long[mercadoCallBacks.size()];
                    for (int index = 0; index < mercadoCallBacks.size(); index++) {
                        MercadoCallBack mercadoCallBack = mercadoCallBacks.get(index);
                        ids[index] = mercadoCallBack.getId();

                        JSONObject message = new JSONObject();
                        message.put("_id", mercadoCallBack.getBackId());
                        message.put("topic", mercadoCallBack.getTopic());
                        message.put("resource", mercadoCallBack.getResource());
                        message.put("user_id", mercadoCallBack.getUserId());
                        message.put("application_id", mercadoCallBack.getApplicationId());

                        amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                                RabbitMQConstant.RoutingKey.MERCADO_CALL_BACK_INFO_MARKETPLACE_ORDERS, message);

                    }
                    // 删除这些数据
                    mercadoCallBackMapper.deleteMercadoCallBackByIds(ids);
                }
            }
        } catch (Exception e) {
            logger.error("同步订单定时任务异常 {}", e);
        } finally {
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("释放锁异常");
            }
        }
        logger.info("同步订单定时任务结束=============");
    }
}
