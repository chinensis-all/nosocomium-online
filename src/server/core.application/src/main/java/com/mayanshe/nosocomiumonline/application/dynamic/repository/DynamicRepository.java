package com.mayanshe.nosocomiumonline.application.dynamic.repository;

import java.util.List;
import java.util.Map;

/**
 * 动态仓库接口。
 * <p>
 * 为动态实体提供对数据库的抽象访问。
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
     * 插入一条记录。
     *
     * @param tableName 表名
     * @param pkName    主键列名
     * @param data      数据映射
     * @return 生成的主键 ID
     */
    int update(String tableName, String pkName, Long id, Map<String, Object> data);

    /**
     * 根据 ID 查询一条记录。
     *
     * @param tableName 表名
     * @param pkName    主键列名
     * @param id        记录 ID
     * @return 数据映射
     */
    Map<String, Object> findById(String tableName, String pkName, Long id);

    /**
     * 查询记录。
     *
     * @param tableName 表名
     * @param criteria  查询条件
     * @param limit     限制条数
     * @param offset    偏移量
     * @return 数据映射列表
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
    List<Map<String, Object>> keysetSearch(String tableName, Map<String, Object> criteria, String keysetColumn, Object keysetValue, int limit);

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
