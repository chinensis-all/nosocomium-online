package com.mayanshe.nosocomiumonline.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis 缓存配置
 */
@Configuration
public class RedisCacheConfig {

    /**
     * 配置 StringRedisTemplate
     * 使用 String 序列化 Key 和 Value，Value 存储 JSON 字符串
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}
