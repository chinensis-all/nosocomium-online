package com.mayanshe.nosocomiumonline.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RedisCacheService 单元测试
 * 使用 MockBean 模拟 StringRedisTemplate，不依赖真实 Redis
 */
@SpringBootTest(classes = { RedisCacheService.class, RedisCacheServiceTest.Config.class })
public class RedisCacheServiceTest {

    @Autowired
    private RedisCacheService redisCacheService;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void remember_shouldReturnCachedValue_whenCacheHit() {
        String key = "test:key:1";
        String cachedJson = "{\"name\":\"test\"}";

        when(valueOperations.get(key)).thenReturn(cachedJson);

        TestDto result = redisCacheService.remember(key, 60, TestDto.class, () -> new TestDto("fail"));

        Assertions.assertNotNull(result);
        Assertions.assertEquals("test", result.getName());
        verify(valueOperations, times(1)).get(key);
        // Should NOT set cache again
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void remember_shouldLoadAndCache_whenCacheMiss() {
        String key = "test:key:2";
        TestDto loaded = new TestDto("loaded");

        when(valueOperations.get(key)).thenReturn(null);

        TestDto result = redisCacheService.remember(key, 60, TestDto.class, () -> loaded);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("loaded", result.getName());

        verify(valueOperations, times(1)).get(key);
        verify(valueOperations, times(1)).set(eq(key), contains("loaded"), eq(60L), eq(TimeUnit.SECONDS));
    }

    @Test
    void remember_shouldNotCache_whenLoaderReturnsNull() {
        String key = "test:key:3";

        when(valueOperations.get(key)).thenReturn(null);

        TestDto result = redisCacheService.remember(key, 60, TestDto.class, () -> null);

        Assertions.assertNull(result);
        verify(valueOperations, times(1)).get(key);
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void remove_shouldDeleteKey() {
        String key = "test:remove";
        redisCacheService.remove(key);
        verify(redisTemplate, times(1)).delete(key);
    }

    @TestConfiguration
    static class Config {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    // Helper DTO class - Public Static for Jackson visibility compatibility in
    // tests
    public static class TestDto {
        private String name;

        public TestDto() {
        }

        public TestDto(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
