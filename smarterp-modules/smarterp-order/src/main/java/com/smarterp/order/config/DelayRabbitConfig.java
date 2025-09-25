package com.smarterp.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author juntao.li
 * @ClassName DelayRabbitConfig
 * @description 延时队列
 * @date 2023/3/27 20:46
 * @Version 1.0
 */
@Configuration
public class DelayRabbitConfig {

    public static final String CLOUD_MERCADO_ORDER_DELAY_QUEUE = "cloud.mercado.order.delay.queue";

    public static final String CLOUD_MERCADO_ORDER_DELAY_EXCHANGE = "cloud.mercado.order.delay.exchange";

    public static final String CLOUD_MERCADO_ORDER_DELAY_ROUTING_KEY = "cloud.mercado.order.delay.routing.key";

    public static final String CLOUD_MERCADO_ORDER_QUEUE = "cloud.mercado.order.queue";

    public static final String CLOUD_MERCADO_ORDER_EXCHANGE = "cloud.mercado.order.exchange";

    public static final String CLOUD_MERCADO_ORDER_ROUTING_KEY = "cloud.mercado.order.routing.key";

    @Bean
    public Queue delayOrderQueue() {
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", CLOUD_MERCADO_ORDER_EXCHANGE);
        params.put("x-dead-letter-routing-key", CLOUD_MERCADO_ORDER_ROUTING_KEY);
        return new Queue(CLOUD_MERCADO_ORDER_DELAY_QUEUE, true, false, false, params);
    }

    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(CLOUD_MERCADO_ORDER_DELAY_EXCHANGE);
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayOrderQueue()).to(orderDelayExchange()).with(CLOUD_MERCADO_ORDER_DELAY_ROUTING_KEY);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(CLOUD_MERCADO_ORDER_QUEUE, true);
    }

    @Bean
    public TopicExchange orderTopicExchange() {
        return new TopicExchange(CLOUD_MERCADO_ORDER_EXCHANGE);
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderTopicExchange()).with(CLOUD_MERCADO_ORDER_ROUTING_KEY);
    }

}
