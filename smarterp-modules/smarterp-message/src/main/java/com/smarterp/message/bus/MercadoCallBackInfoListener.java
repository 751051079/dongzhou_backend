package com.smarterp.message.bus;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.message.constant.RabbitMQConstant;
import com.smarterp.message.domain.MercadoCallBack;
import com.smarterp.message.mapper.MercadoCallBackMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MercadoCallBackInfoListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoCallBackInfoListener.class);

    @Resource
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private MercadoCallBackMapper mercadoCallBackMapper;

    /**
     * 处理美客多回调消息
     *
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.MERCADO_CALL_BACK_INFO_QUEUE),
            key = RabbitMQConstant.RoutingKey.MERCADO_CALL_BACK_INFO))
    public void mercadoCallBackInfoListener(JSONObject message) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("MERCADO_CALL_BACK_INFO_LOCK:" + message.getString("_id"));
        boolean b = lock.tryLock();
        try {
            if (b) {
                if (message != null) {
                    MercadoCallBack mercadoCallBack = new MercadoCallBack();
                    mercadoCallBack.setId(IdUtil.getSnowflake(1, 1).nextId());
                    mercadoCallBack.setBackId(message.getString("_id"));
                    mercadoCallBack.setTopic(message.getString("topic"));
                    mercadoCallBack.setResource(message.getString("resource"));
                    mercadoCallBack.setUserId(message.getString("user_id"));
                    mercadoCallBack.setApplicationId(message.getString("application_id"));
                    mercadoCallBack.setSent(message.getDate("sent"));
                    mercadoCallBack.setAttempts(message.getString("attempts"));
                    mercadoCallBack.setReceived(message.getDate("received"));
                    mercadoCallBackMapper.insertMercadoCallBack(mercadoCallBack);
                }
            } else {
                logger.error("处理美客多回调消息没有拿到锁 message {}", message.toJSONString());
            }
        } catch (Exception e) {
            logger.error("处理美客多回调消息异常 {} message {}", e, message.toJSONString());
        } finally {
            long end = System.currentTimeMillis();
            logger.error("处理美客多回调消息耗费时间 {} message {}", (end - startTime), message.toJSONString());
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("处理美客多回调消息释放锁异常 {} message {}", e, message.toJSONString());
            }
        }
    }

}
