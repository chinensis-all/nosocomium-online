package com.mayanshe.nosocomiumonline.application.dynamic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayanshe.nosocomiumonline.application.messaging.EventPublisher;
import com.mayanshe.nosocomiumonline.shared.contract.ICache;
import com.mayanshe.nosocomiumonline.shared.event.IntegrationEvent;
import com.mayanshe.nosocomiumonline.shared.contract.IdAccessor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicCrudServiceTest {

    @Mock
    private DynamicRepository dynamicRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private ICache cache;

    @Mock
    private DynamicCrudConfigRegistry configRegistry;

    @InjectMocks
    private DynamicCrudService dynamicCrudService;

    // Explicitly mocked ObjectMapper not needed if we rely on static instance in
    // service,
    // but we can verify event publisher receives something valid.

    @Getter
    @Setter
    public static class TestEntity implements IdAccessor {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    public static class TestDto implements IdAccessor {
        private Long id;
        private String name;
    }

    private CrudConfig<TestEntity, TestDto> config;

    @BeforeEach
    void setUp() {
        config = CrudConfig.<TestEntity, TestDto>builder()
                .name("test_entity")
                .title("Test Entity")
                .entityType(TestEntity.class)
                .dtoType(TestDto.class)
                .publishEvents(true)
                .isSoftDelete(true)
                .enableDetailCache(true)
                .detailCacheTTL(600)
                .build();
    }

    @Test
    void create_shouldPublishEvent() {
        when(configRegistry.get("test_entity")).thenReturn((CrudConfig) config);

        Map<String, Object> data = new HashMap<>();
        data.put("name", "test");

        when(dynamicRepository.insert(any(TestEntity.class), eq(TestEntity.class))).thenReturn(1L);

        long id = dynamicCrudService.create("test_entity", data);

        verify(dynamicRepository).insert(any(TestEntity.class), eq(TestEntity.class));
        verify(eventPublisher).publish(any(IntegrationEvent.class));
    }

    @Test
    void destroy_shouldSoftDeleteAndPublishEvent() {
        when(configRegistry.get("test_entity")).thenReturn((CrudConfig) config);

        dynamicCrudService.destroy("test_entity", 1L);

        verify(dynamicRepository).softDeleteById(eq(1L), eq(TestEntity.class));
        verify(eventPublisher).publish(any(IntegrationEvent.class));
        verify(cache).remove(eq("TestEntity:id=1"));
    }

    @Test
    void find_shouldUseCache() {
        when(configRegistry.get("test_entity")).thenReturn((CrudConfig) config);

        TestDto cachedDto = new TestDto();
        cachedDto.setId(1L);
        cachedDto.setName("cached");

        // Mock Cache Hit behavior simulation (since cache.remember invokes callback on
        // miss)
        // Here we just test that cache.remember is called with correct arguments

        dynamicCrudService.find("test_entity", 1L);

        verify(cache).remember(eq("TestEntity:id=1"), eq(600L), eq(TestDto.class), any());
    }

    @Test
    void find_shouldCallRepoWhenCacheMiss() {
        // This is harder to test with functional interface `remember`, typically we
        // test that `remember` is called.
        // To test inner logic we'd need to invoke the callback passed to `remember`.

        // Let's just verify repository interaction if we call the method that doesn't
        // use cache
        // OR mock cache to execute the supplier.

        when(configRegistry.get("test_entity")).thenReturn((CrudConfig) config);
        when(cache.remember(anyString(), anyLong(), any(), any())).thenAnswer(invocation -> {
            java.util.function.Supplier<?> supplier = invocation.getArgument(3);
            return supplier.get();
        });

        TestEntity entity = new TestEntity();
        entity.setId(1L);
        entity.setName("db");

        when(dynamicRepository.findById(eq(1L), eq(TestEntity.class))).thenReturn(entity);

        TestDto result = dynamicCrudService.find("test_entity", 1L);

        assertEquals("db", result.getName());
        verify(dynamicRepository).findById(eq(1L), eq(TestEntity.class));
    }
}
