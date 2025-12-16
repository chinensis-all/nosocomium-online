package com.mayanshe.nosocomiumonline.infrastructure.persistence.dynamic;

import com.mayanshe.nosocomiumonline.application.dynamic.DynamicMapperRegistry;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper.CrudMapper;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.repository.DynamicRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicRepositoryImplTest {

    @Mock
    private DynamicMapperRegistry dynamicMapperRegistry;

    @Mock
    private CrudMapper<TestEntity> crudMapper;

    @InjectMocks
    private DynamicRepositoryImpl dynamicRepository;

    private static class TestEntity {
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void count_shouldDelegateToMapper() {
        Map<String, Object> criteria = new HashMap<>();
        Class<TestEntity> type = TestEntity.class;

        // Mock registry to return our mock mapper
        when(dynamicMapperRegistry.getMapper(eq(type))).thenReturn(crudMapper);
        // Mock mapper typing check (CrudMapper<E> usually has entityType() or similar
        // if generic)
        // In DynamicRepositoryImpl: if (mapperObj instanceof CrudMapper mapper &&
        // mapper.entityType().equals(type))
        // So we need to mock entityType()
        doReturn(type).when(crudMapper).entityType();
        when(crudMapper.count(eq(criteria))).thenReturn(5L);

        long count = dynamicRepository.count(criteria, type);

        assertEquals(5L, count);
        verify(crudMapper).count(eq(criteria));
    }
}
