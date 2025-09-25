package com.smarterp.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * @author juntao.li
 * @ClassName RedisLockConfig
 * @description 分布式锁
 * @date 2023/7/7 15:49
 * @Version 1.0
 */
@Configuration
public class RedisLockConfig {

    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, "CCTP_LOCK");
    }
}
