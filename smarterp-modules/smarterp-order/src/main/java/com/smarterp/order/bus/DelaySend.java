package com.smarterp.order.bus;

import com.smarterp.order.config.DelayRabbitConfig;
import com.smarterp.order.domain.MercadoOrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author juntao.li
 * @ClassName DelaySend
 * @description 延时队列
 * @date 2023/3/27 21:09
 * @Version 1.0
 */
@Component
public class DelaySend {

    private final static Logger logger = LoggerFactory.getLogger(DelaySend.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    public void sendDelayOrder(MercadoOrderInfo mercadoOrder) {
        logger.info("消息进入延时队列等待，开始时间 {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        amqpTemplate.convertAndSend(DelayRabbitConfig.CLOUD_MERCADO_ORDER_DELAY_EXCHANGE, DelayRabbitConfig.CLOUD_MERCADO_ORDER_DELAY_ROUTING_KEY,
                mercadoOrder, message -> {
                    message.getMessageProperties().setExpiration(String.valueOf(10 * 1000));
                    return message;
                });
    }

    public void sendProductSite(Long mercadoProductItemId, String exp) {
        logger.info("发布站点消息进入延时队列等待，开始时间 {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        amqpTemplate.convertAndSend(DelayRabbitConfig.CLOUD_MERCADO_ORDER_DELAY_EXCHANGE, DelayRabbitConfig.CLOUD_MERCADO_ORDER_DELAY_ROUTING_KEY,
                mercadoProductItemId, message -> {
                    message.getMessageProperties().setExpiration(exp);
                    return message;
                });
    }
}
