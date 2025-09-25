package com.smarterp.order.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author juntao.li
 * @ClassName RabbitConfig
 * @description
 * @date 2023/3/23 22:02
 * @Version 1.0
 */
@Configuration
public class RabbitConfig {

    // 并发数量:根据实际的服务器性能进行配置，计算型可以参考下边代码。
    public static final int DEFAULT_CONCURRENT = Runtime.getRuntime().availableProcessors() + 1;

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
