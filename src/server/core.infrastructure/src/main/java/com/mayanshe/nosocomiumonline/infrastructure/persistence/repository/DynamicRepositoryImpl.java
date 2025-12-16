package com.mayanshe.nosocomiumonline.infrastructure.persistence.repository;

import com.mayanshe.nosocomiumonline.application.dynamic.DynamicMapperRegistry;
import com.mayanshe.nosocomiumonline.application.dynamic.DynamicRepository;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper.CrudMapper;
import com.mayanshe.nosocomiumonline.shared.valueobject.Ascending;
import com.mayanshe.nosocomiumonline.shared.valueobject.Direction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 动态仓库实现类。
 * <p>
 * 提供对动态实体的数据库操作实现。
 */
@Repository
@RequiredArgsConstructor
public class DynamicRepositoryImpl implements DynamicRepository {

    private final DynamicMapperRegistry dynamicMapperRegistry;

    @Override
    @SuppressWarnings("unchecked")
    public long insert(Object entity, Type type) {
        Object mapperObj = dynamicMapperRegistry.getMapper(type);
        if (mapperObj instanceof CrudMapper mapper && mapper.entityType().equals(type)) {
            return mapper.insert(entity);
        }

        throw new UnsupportedOperationException("Insert operation is not supported for type: " + type.getTypeName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public long update(Object entity, Type type) {
        Object mapperObj = dynamicMapperRegistry.getMapper(type);
        if (mapperObj instanceof CrudMapper mapper && mapper.entityType().equals(type)) {
            return mapper.update(entity);
        }

        throw new UnsupportedOperationException("Update operation is not supported for type: " + type.getTypeName());
    }

    @Override
    public long deleteById(Long id, Type type) {
        Object mapperObj = dynamicMapperRegistry.getMapper(type);
        if (mapperObj instanceof CrudMapper mapper && mapper.entityType().equals(type)) {
            return mapper.deleteById(id);
        }

        throw new UnsupportedOperationException("Delete operation is not supported yet.");
    }

    @Override
    public long softDeleteById(Long id, Type type) {
        Object mapperObj = dynamicMapperRegistry.getMapper(type);
        if (mapperObj instanceof CrudMapper mapper && mapper.entityType().equals(type)) {
            return mapper.softDeleteById(id);
        }

        throw new UnsupportedOperationException("Soft delete operation is not supported yet.");
    }

    @Override
    public Object findById(Long id, Type type) {
        Object mapperObj = dynamicMapperRegistry.getMapper(type);
        if (mapperObj instanceof CrudMapper mapper && mapper.entityType().equals(type)) {
            return mapper.findById(id);
        }

        throw new UnsupportedOperationException("Find by ID operation is not supported yet.");
    }

    @Override
    public List<Object> search(Map<String, Object> criteria, int limit, int offset, Type type) {
        limit = Math.max(limit, 1);
        limit = Math.min(limit, 100); // 限死百条
        offset = Math.max(offset, 0);

        Object mapperObj = dynamicMapperRegistry.getMapper(type);
        if (mapperObj instanceof CrudMapper mapper && mapper.entityType().equals(type)) {
            return mapper.search(criteria, limit, offset);
        }

        throw new UnsupportedOperationException("Search operation is not supported yet.");
    }

    @Override
    public long count(Map<String, Object> criteria, Type type) {
        Object mapperObj = dynamicMapperRegistry.getMapper(type);
        if (mapperObj instanceof CrudMapper mapper && mapper.entityType().equals(type)) {
            return mapper.count(criteria);
        }

        throw new UnsupportedOperationException("Count operation is not supported yet.");
    }

    @Override
    public List<Object> keysetSearch(Map<String, Object> criteria, Ascending ascending, Direction direction, Long id, int limit, Type type) {
        limit = Math.max(limit, 1);
        limit = Math.min(limit, 100); // 限死百条

        Object mapperObj = dynamicMapperRegistry.getMapper(type);
        if (mapperObj instanceof CrudMapper mapper && mapper.entityType().equals(type)) {
            Boolean isAsc = ascending == Ascending.ASC;
            Boolean isNext = direction == Direction.NEXT;
            return mapper.keysetSearch(criteria, isAsc, isNext, id, limit);
        }

        throw new UnsupportedOperationException("Keyset search operation is not supported yet.");
    }
}
