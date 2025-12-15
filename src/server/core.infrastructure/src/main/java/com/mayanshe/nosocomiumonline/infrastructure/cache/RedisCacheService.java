package com.mayanshe.nosocomiumonline.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayanshe.nosocomiumonline.shared.contract.ICache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 基于 StringRedisTemplate 的简单缓存实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RedisCacheService implements ICache {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public <T> T remember(String key, long ttlSeconds, Class<T> type, Supplier<T> loader) {
        // 1. 尝试从缓存获取
        String json = redisTemplate.opsForValue().get(key);

        if (StringUtils.hasText(json)) {
            try {
                return objectMapper.readValue(json, type);
            } catch (JsonProcessingException e) {
                log.error("Cache deserialization failed for key: {}", key, e);
                // 反序列化失败，视为缓存未命中或脏数据，可以选择删除缓存并重新加载
                remove(key);
                throw new RuntimeException("Cache deserialization failed for key: " + key, e);
            }
        }

        // 2. 缓存未命中，调用 loader 加载
        T value = loader.get();

        // 3. 结果不为 null 则写入缓存
        if (value != null) {
            try {
                String valueJson = objectMapper.writeValueAsString(value);
                redisTemplate.opsForValue().set(key, valueJson, ttlSeconds, TimeUnit.SECONDS);
            } catch (JsonProcessingException e) {
                log.error("Cache serialization failed for key: {}", key, e);
                throw new RuntimeException("Cache serialization failed for key: " + key, e);
            }
        }

        return value;
    }
}
