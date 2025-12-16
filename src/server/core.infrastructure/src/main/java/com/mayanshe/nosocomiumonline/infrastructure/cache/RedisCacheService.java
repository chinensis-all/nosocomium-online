package com.mayanshe.nosocomiumonline.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayanshe.nosocomiumonline.shared.contract.ICache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * RedisCacheService: 基于 StringRedisTemplate 的简单缓存实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RedisCacheService implements ICache {

    private static final String LOCK_SUFFIX = ":lock";
    private static final long LOCK_EXPIRE_SECONDS = 10;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public <T> T remember(String key, long ttlSeconds, Type type, Supplier<T> loader) {

        // 1. 第一次读缓存
        T cached = getFromCache(key, type);
        if (cached != null) {
            return cached;
        }

        String lockKey = key + LOCK_SUFFIX;
        boolean locked = tryLock(lockKey);

        try {
            if (locked) {
                // 2. 获得锁后，二次检查缓存（非常重要）
                cached = getFromCache(key, type);
                if (cached != null) {
                    return cached;
                }

                // 3. 真正执行 loader
                T value = loader.get();

                if (value != null) {
                    putToCache(key, value, ttlSeconds);
                }

                return value;
            } else {
                // 4. 未拿到锁，短暂自旋等待
                return spinWaitAndGet(key, type);
            }
        } finally {
            if (locked) {
                unlock(lockKey);
            }
        }
    }

    private <T> T getFromCache(String key, Type type) {
        String json = redisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(json)) {
            return null;
        }

        try {
            return objectMapper.readValue(json, objectMapper.constructType(type));
        } catch (Exception e) {
            log.warn("Cache deserialization failed, remove dirty cache. key={}", key, e);
            remove(key);
            return null;
        }
    }

    private void putToCache(String key, Object value, long ttlSeconds) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Cache serialization failed. key={}", key, e);
            // 写缓存失败不影响主流程
        }
    }

    private boolean tryLock(String lockKey) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    private void unlock(String lockKey) {
        redisTemplate.delete(lockKey);
    }

    /**
     * 未获取锁的线程，短暂等待并重试
     */
    private <T> T spinWaitAndGet(String key, Type type) {
        int retry = 5;
        long sleepMs = 50;

        for (int i = 0; i < retry; i++) {
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            T cached = getFromCache(key, type);
            if (cached != null) {
                return cached;
            }
        }

        // 最终兜底：直接走 loader（避免一直阻塞）
        log.warn("Cache spin wait failed, fallback to loader. key={}", key);
        return null;
    }
}
