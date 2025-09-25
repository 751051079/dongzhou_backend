package com.smarterp.order.bus;

import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.mapper.MercadoBatchCollectionLinkMapper;
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
public class MercadoUpdateUpcListener {

    private static final Logger logger = LoggerFactory.getLogger(MercadoUpdateUpcListener.class);

    @Resource
    private MercadoBatchCollectionLinkMapper mercadoBatchCollectionLinkMapper;

    @Resource
    private RedisLockRegistry redisLockRegistry;

    /**
     * 更新upc编码可用数和已用数量
     *
     * @param upcId
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.UPDATE_UPC_CODE_INFO_QUEUE),
            key = RabbitMQConstant.RoutingKey.UPDATE_UPC_CODE_INFO))
    public void batchCollectionLink(Long upcId) {
        // 开始时间
        long startTime = System.currentTimeMillis();
        Lock lock = redisLockRegistry.obtain("UPDATE_UPC_CODE_INFO:" + upcId);
        boolean b = lock.tryLock();
        try {
            if (b) {
                if (upcId != null) {
                    mercadoBatchCollectionLinkMapper.updateUpcCount(upcId);
                }
            } else {
                logger.error("更新upc编码可用数和已用数量没有拿到锁 upcId {}", upcId);
            }
        } catch (Exception e) {
            logger.error("更新upc编码可用数和已用数量异常 {} upcId {}", e, upcId);
        } finally {
            long end = System.currentTimeMillis();
            logger.error("更新upc编码可用数和已用数量耗费时间 {} upcId {}", (end - startTime), upcId);
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("更新upc编码可用数和已用数量释放锁异常 {} upcId {}", e, upcId);
            }
        }
    }

}
