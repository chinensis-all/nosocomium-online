package com.mayanshe.nosocomiumonline.infrastructure.persistence.dynamic;

import com.mayanshe.nosocomiumonline.application.dynamic.repository.DynamicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Implementation of DynamicRepository.
 */
@Repository
@RequiredArgsConstructor
public class DynamicRepositoryImpl implements DynamicRepository {

    private final DynamicMapper dynamicMapper;

    @Override
    public Long insert(String tableName, String pkName, Map<String, Object> data) {
        dynamicMapper.insert(tableName, pkName, data);
        // Assuming keyProperty="_data.id" populated the ID in the map
        Object id = data.get("id");
        if (id instanceof Long) {
            return (Long) id;
        } else if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        return null;
    }

    @Override
    public int update(String tableName, String pkName, Long id, Map<String, Object> data) {
        return dynamicMapper.update(tableName, pkName, id, data);
    }

    @Override
    public Map<String, Object> findById(String tableName, String pkName, Long id) {
        return dynamicMapper.findById(tableName, pkName, id);
    }

    @Override
    public List<Map<String, Object>> search(String tableName, Map<String, Object> criteria, int limit, int offset) {
        return dynamicMapper.search(tableName, criteria, limit, offset);
    }

    @Override
    public long count(String tableName, Map<String, Object> criteria) {
        return dynamicMapper.count(tableName, criteria);
    }

    @Override
    public List<Map<String, Object>> keysetSearch(String tableName, Map<String, Object> criteria, String keysetColumn,
            Object keysetValue, int limit) {
        return dynamicMapper.keysetSearch(tableName, criteria, keysetColumn, keysetValue, limit);
    }

    @Override
    public int delete(String tableName, String pkName, Long id) {
        return dynamicMapper.delete(tableName, pkName, id);
    }

    @Override
    public int softDelete(String tableName, String pkName, Long id) {
        // Using System.currentTimeMillis() or Instant for consistency
        return dynamicMapper.softDelete(tableName, pkName, id, System.currentTimeMillis());
    }
}
