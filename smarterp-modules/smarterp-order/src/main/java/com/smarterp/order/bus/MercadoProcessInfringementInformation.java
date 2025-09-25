package com.smarterp.order.bus;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.deepseek.DeepSeekUtils;
import com.smarterp.order.constant.RabbitMQConstant;
import com.smarterp.order.domain.MercadoProduct;
import com.smarterp.order.mapper.MercadoProductMapper;
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
public class MercadoProcessInfringementInformation {

    private static final Logger logger = LoggerFactory.getLogger(MercadoProcessInfringementInformation.class);

    @Resource
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private MercadoProductMapper mercadoProductMapper;

    /**
     * 处理侵权信息
     *
     * @param productId
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.CLOUD_PROCESS_INFRINGEMENT_INFORMATION_QUEUE),
            key = RabbitMQConstant.RoutingKey.CLOUD_PROCESS_INFRINGEMENT_INFORMATION))
    public void mercadoProcessInfringementInformation(Long productId) {
        logger.info("开始处理侵权信息 产品ID {}", productId);
        Lock lock = redisLockRegistry.obtain("PROCESS:INFRINGEMENT:GLOBAL:PRODUCT:" + productId);
        boolean b = lock.tryLock();
        try {
            if (b) {
                MercadoProduct mercadoProduct = mercadoProductMapper.selectMercadoProductById(productId);
                if (mercadoProduct != null) {
                    JSONObject jsonObject = DeepSeekUtils.sendMessage(mercadoProduct.getMercadoProductUrl());
                    logger.info("调用deepseek模型判断产品是否侵权返回信息 {}", jsonObject.toJSONString());
                    if (jsonObject != null) {
                        JSONArray choices = jsonObject.getJSONArray("choices");
                        if (choices != null && choices.size() > 0) {
                            JSONObject object = choices.getJSONObject(0);
                            JSONObject message = object.getJSONObject("message");
                            if (message != null) {
                                String content = message.getString("content");
                                if (content != null) {
                                    String[] lines = content.split("\n");
                                    // 获取第一行
                                    String firstLine = lines[0];

                                    MercadoProduct example = new MercadoProduct();
                                    example.setId(mercadoProduct.getId());
                                    if (firstLine.contains("可能侵权")) {
                                        example.setInfringementStatus("可能侵权");
                                    } else if (firstLine.contains("不会侵权")) {
                                        example.setInfringementStatus("不会侵权");
                                    } else if (firstLine.contains("一定侵权")) {
                                        example.setInfringementStatus("一定侵权");
                                    }
                                    example.setInfringementInfo(content);
                                    mercadoProductMapper.updateMercadoProduct(example);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("处理是否侵权产品信息异常 产品id {} 异常信息 {}", productId, e);
        } finally {
            try {
                if (b) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.error("处理是否侵权产品信息释放锁异常 {}", e);
            }
        }


    }


}
