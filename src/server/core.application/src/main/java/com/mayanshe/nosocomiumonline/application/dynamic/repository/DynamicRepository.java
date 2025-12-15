package com.mayanshe.nosocomiumonline.application.dynamic.repository;

import java.util.List;
import java.util.Map;

/**
 * Interface for Dynamic Repository.
 * <p>
 * Provides abstract access to the database for dynamic entities.
 */
public interface DynamicRepository {

    /**
     * Inserts a record.
     *
     * @param tableName table name
     * @param pkName    primary key column name
     * @param data      data map
     * @return generated ID
     */
    Long insert(String tableName, String pkName, Map<String, Object> data);

    /**
     * Updates a record.
     *
     * @param tableName table name
     * @param pkName    primary key column name
     * @param id        record ID
     * @param data      data map
     * @return rows affected
     */
    int update(String tableName, String pkName, Long id, Map<String, Object> data);

    /**
     * Finds a record by ID.
     *
     * @param tableName table name
     * @param pkName    primary key column name
     * @param id        record ID
     * @return data map
     */
    Map<String, Object> findById(String tableName, String pkName, Long id);

    /**
     * Searches for records.
     *
     * @param tableName table name
     * @param criteria  search criteria
     * @param limit     limit
     * @param offset    offset
     * @return list of data maps
     */
    List<Map<String, Object>> search(String tableName, Map<String, Object> criteria, int limit, int offset);

    /**
     * Counts records matching criteria.
     *
     * @param tableName table name
     * @param criteria  search criteria
     * @return count
     */
    long count(String tableName, Map<String, Object> criteria);

    /**
     * Keyset Search for pagination.
     * 
     * @param tableName    table name
     * @param criteria     search criteria (filters)
     * @param keysetColumn column for keyset (e.g. id, created_at)
     * @param keysetValue  value of the last seen record
     * @param limit        limit
     * @return list of data maps
     */
    List<Map<String, Object>> keysetSearch(String tableName, Map<String, Object> criteria, String keysetColumn,
            Object keysetValue, int limit);

    /**
     * Physically deletes a record.
     * 
     * @param tableName table name
     * @param pkName    primary key column name
     * @param id        record ID
     * @return rows affected
     */
    int delete(String tableName, String pkName, Long id);

    /**
     * Soft deletes a record (sets deleted_at).
     * 
     * @param tableName table name
     * @param pkName    primary key column name
     * @param id        record ID
     * @return rows affected
     */
    int softDelete(String tableName, String pkName, Long id);
}
