package com.mayanshe.nosocomiumonline.infrastructure.persistence.dynamic;

import com.mayanshe.nosocomiumonline.shared.util.AnsiColor;
import com.mayanshe.nosocomiumonline.shared.util.PrintUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * MyBatis SQL Provider for Dynamic CRUD.
 */
public class DynamicSqlProvider {

    public String insert(Map<String, Object> params) {
        String tableName = (String) params.get("_tableName");
        String pkName = (String) params.get("_pkName");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) params.get("_data");

        SQL sql = new SQL().INSERT_INTO(tableName);

        for (String column : data.keySet()) {
            sql.VALUES(column, "#{_data." + column + "}");
        }

        return sql.toString();
    }

    public String update(Map<String, Object> params) {
        String tableName = (String) params.get("_tableName");
        String pkName = (String) params.get("_pkName");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) params.get("_data");
        Long id = (Long) params.get("_id");

        SQL sql = new SQL().UPDATE(tableName);
        for (String column : data.keySet()) {
            sql.SET(column + " = #{_data." + column + "}");
        }
        sql.WHERE(pkName + " = #{_id}");

        return sql.toString();
    }

    public String findById(Map<String, Object> params) {
        String tableName = (String) params.get("_tableName");
        String pkName = (String) params.get("_pkName");

        return new SQL()
                .SELECT("*")
                .FROM(tableName)
                .WHERE(pkName + " = #{_id}")
                .toString();
    }

    public String search(Map<String, Object> params) {
        String tableName = (String) params.get("_tableName");
        @SuppressWarnings("unchecked")
        Map<String, Object> criteria = (Map<String, Object>) params.get("_criteria");
        Integer limit = (Integer) params.get("_limit");
        Integer offset = (Integer) params.get("_offset");

        SQL sql = new SQL().SELECT("*").FROM(tableName);

        if (criteria != null) {
            for (String key : criteria.keySet()) {
                Object value = criteria.get(key);
                if (value != null) {
                    // Basic equality check. For more complex queries, this needs extension.
                    sql.WHERE(key + " = #{_criteria." + key + "}");
                }
            }
        }

        String query = sql.toString();
        if (limit != null && offset != null) {
            query += " LIMIT " + limit + " OFFSET " + offset;
        }
        return query;
    }

    public String count(Map<String, Object> params) {
        String tableName = (String) params.get("_tableName");
        @SuppressWarnings("unchecked")
        Map<String, Object> criteria = (Map<String, Object>) params.get("_criteria");

        SQL sql = new SQL().SELECT("count(*)").FROM(tableName);

        if (criteria != null) {
            for (String key : criteria.keySet()) {
                Object value = criteria.get(key);
                if (value != null) {
                    sql.WHERE(key + " = #{_criteria." + key + "}");
                }
            }
        }
        return sql.toString();
    }

    public String keysetSearch(Map<String, Object> params) {
        String tableName = (String) params.get("_tableName");
        @SuppressWarnings("unchecked")
        Map<String, Object> criteria = (Map<String, Object>) params.get("_criteria");
        String keysetColumn = (String) params.get("_keysetColumn");
        Object keysetValue = params.get("_keysetValue");
        Integer limit = (Integer) params.get("_limit");

        SQL sql = new SQL().SELECT("*").FROM(tableName);

        if (criteria != null) {
            for (String key : criteria.keySet()) {
                Object value = criteria.get(key);
                if (value != null) {
                    sql.WHERE(key + " = #{_criteria." + key + "}");
                }
            }
        }

        if (keysetColumn != null && keysetValue != null) {
            // Assuming ascending order for now: column > value
            // Ideally explicit direction needed. Defaulting to > (next page).
            sql.WHERE(keysetColumn + " > #{_keysetValue}");
            sql.ORDER_BY(keysetColumn + " ASC");
        } else if (keysetColumn != null) {
            sql.ORDER_BY(keysetColumn + " ASC");
        }

        String query = sql.toString();
        if (limit != null) {
            query += " LIMIT " + limit;
        }
        if (limit != null) {
            query += " LIMIT " + limit;
        }
        return query;
    }

    public String delete(Map<String, Object> params) {
        String tableName = (String) params.get("_tableName");
        String pkName = (String) params.get("_pkName");

        return new SQL()
                .DELETE_FROM(tableName)
                .WHERE(pkName + " = #{_id}")
                .toString();
    }

    public String softDelete(Map<String, Object> params) {
        String tableName = (String) params.get("_tableName");
        String pkName = (String) params.get("_pkName");

        return new SQL()
                .UPDATE(tableName)
                .SET("deleted_at = #{_now}") // Expecting _now to be passed, or use DB function like NOW()
                .WHERE(pkName + " = #{_id}")
                .toString();
    }
}
