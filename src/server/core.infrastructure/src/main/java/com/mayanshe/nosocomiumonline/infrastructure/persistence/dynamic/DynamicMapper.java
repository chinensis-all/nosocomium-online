package com.mayanshe.nosocomiumonline.infrastructure.persistence.dynamic;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

/**
 * MyBatis Mapper for Dynamic Operations.
 */
@Mapper
public interface DynamicMapper {

        @InsertProvider(type = DynamicSqlProvider.class, method = "insert")
        @Options(useGeneratedKeys = true, keyProperty = "_data.id", keyColumn = "id")
        // WARN: keyProperty might need adjustment if Entity uses logic ID or specific
        // PK name.
        // For now, assuming 'id' property in Map. My Provider uses #{_data.col}.
        // If database generates ID, it tries to set it back to parameter.
        // Since parameter is a Map wrapper, setting back to nested Map is tricky in
        // MyBatis.
        // We might need to handle ID generation explicitly or assume auto-increment and
        // re-fetch if basic keyProperty fails.
        // Let's rely on standard behavior or user explicit ID for now.
        void insert(@Param("_tableName") String tableName, @Param("_pkName") String pkName,
                        @Param("_data") Map<String, Object> data);

        @UpdateProvider(type = DynamicSqlProvider.class, method = "update")
        int update(@Param("_tableName") String tableName, @Param("_pkName") String pkName, @Param("_id") Long id,
                        @Param("_data") Map<String, Object> data);

        @SelectProvider(type = DynamicSqlProvider.class, method = "findById")
        Map<String, Object> findById(@Param("_tableName") String tableName, @Param("_pkName") String pkName,
                        @Param("_id") Long id);

        @SelectProvider(type = DynamicSqlProvider.class, method = "search")
        List<Map<String, Object>> search(@Param("_tableName") String tableName,
                        @Param("_criteria") Map<String, Object> criteria, @Param("_limit") int limit,
                        @Param("_offset") int offset);

        @SelectProvider(type = DynamicSqlProvider.class, method = "count")
        long count(@Param("_tableName") String tableName, @Param("_criteria") Map<String, Object> criteria);

        @SelectProvider(type = DynamicSqlProvider.class, method = "keysetSearch")
        List<Map<String, Object>> keysetSearch(@Param("_tableName") String tableName,
                        @Param("_criteria") Map<String, Object> criteria,
                        @Param("_keysetColumn") String keysetColumn,
                        @Param("_keysetValue") Object keysetValue,
                        @Param("_limit") int limit);

        @DeleteProvider(type = DynamicSqlProvider.class, method = "delete")
        int delete(@Param("_tableName") String tableName, @Param("_pkName") String pkName, @Param("_id") Long id);

        @UpdateProvider(type = DynamicSqlProvider.class, method = "softDelete")
        int softDelete(@Param("_tableName") String tableName, @Param("_pkName") String pkName, @Param("_id") Long id,
                        @Param("_now") Long now);
}
