package com.mayanshe.nosocomiumonline.application.dynamic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayanshe.nosocomiumonline.application.dynamic.config.CrudConfig;
import com.mayanshe.nosocomiumonline.application.dynamic.repository.DynamicRepository;
import com.mayanshe.nosocomiumonline.application.messaging.EventPublisher;
import com.mayanshe.nosocomiumonline.shared.event.IntegrationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicCrudServiceTest {

    @Mock
    private DynamicRepository dynamicRepository;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private DynamicCrudService dynamicCrudService;

    static class TestEntity {
    }

    @BeforeEach
    void setUp() {
        // Mock Redis ops
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        CrudConfig<TestEntity> config = CrudConfig.<TestEntity>builder()
                .name("test_entity")
                .entityType(TestEntity.class)
                .tableName("test_table")
                .mapToEntity(map -> new TestEntity())
                .enableFindCache(true)
                .publishEvents(true)
                .isSoftDelete(true)
                .build();
        dynamicCrudService.register(config);
    }

    @Test
    void create_shouldPublishEvent() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "test");

        when(dynamicRepository.insert(eq("test_table"), eq("id"), eq(data))).thenReturn(100L);

        dynamicCrudService.create("test_entity", data);

        verify(dynamicRepository).insert(eq("test_table"), eq("id"), eq(data));
        verify(eventPublisher).publish(any(IntegrationEvent.class));
    }

    @Test
    void destroy_shouldSoftDeleteAndPublishEvent() {
        dynamicCrudService.destroy("test_entity", 1L);

        verify(dynamicRepository).softDelete(eq("test_table"), eq("id"), eq(1L));
        verify(eventPublisher).publish(any(IntegrationEvent.class));
        verify(redisTemplate).delete(eq("test_entity:id=1"));
    }

    @Test
    void find_shouldUseCache() throws JsonProcessingException {
        // Mock Cache Miss
        when(valueOperations.get("test_entity:id=1")).thenReturn(null);
        when(dynamicRepository.findById("test_table", "id", 1L)).thenReturn(Map.of("id", 1));

        dynamicCrudService.findRaw("test_entity", 1L);

        verify(dynamicRepository).findById("test_table", "id", 1L);
        verify(valueOperations).set(eq("test_entity:id=1"), anyString(), eq(600L), eq(TimeUnit.SECONDS));

        // Mock Cache Hit
        when(valueOperations.get("test_entity:id=1")).thenReturn("{\"id\":1}");
        dynamicCrudService.findRaw("test_entity", 1L);

        // Repository should not be called again
        verify(dynamicRepository, times(1)).findById("test_table", "id", 1L);
    }
}
