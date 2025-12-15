package com.mayanshe.nosocomiumonline.application.dynamic.config;

import com.mayanshe.nosocomiumonline.shared.contract.ICreateCommandToEntity;
import com.mayanshe.nosocomiumonline.shared.contract.IEntityToDto;
import com.mayanshe.nosocomiumonline.shared.contract.IMapToEntity;
import com.mayanshe.nosocomiumonline.shared.contract.IModifyCommandToEntity;
import lombok.Builder;
import lombok.Getter;

/**
 * 动态CRUD服务配置
 *
 * @param <E> Entity类型
 */
@Getter
@Builder
public class CrudConfig<E> {
    private final String name;                                             // 配置名称
    private final String title;                                            // 配置标题,用于错误输出

    private final Class<E> entityType;                                     // Entity类型
    private final String tableName;                                        // 数据库表名
    @Builder.Default                                                       // 主键名称，默认为"id"
    private final String pkName = "id";                                    // 主键名称

    // Converters
    private final IMapToEntity<E> mapToEntity;                             // Map到Entity的转换器
    private final IEntityToDto<E, ?> entityToDto;                          // Entity到DTO的转换器
    private final ICreateCommandToEntity<?, E> createCommandToEntity;      // 创建Command到Entity的转换器
    private final IModifyCommandToEntity<?, E> modifyCommandToEntity;      // 修改Command到Entity的转换器

    // Feature Flags
    @Builder.Default
    private final boolean isSoftDelete = false;                            // 是否启用软删除
    @Builder.Default
    private final boolean publishEvents = true;                            // 是否发布事件

    // Cache Settings
    @Builder.Default
    private final boolean enableDetailCache = false;                       // 是否启用Detail缓存
    @Builder.Default
    private final long detailCacheTTL = 600;                               // Detail缓存TTL，单位秒

    @Builder.Default
    private final boolean enableListCache = true;                          // 是否启用List缓存
    @Builder.Default
    private final long listCacheTtl = 60; // Seconds

    public String getName() {
        return name != null ? name.toLowerCase() : tableName.toLowerCase();
    }

    public void validate() {
        if (entityType == null)
            throw new IllegalArgumentException("Entity type cannot be null");
        if (tableName == null || tableName.isEmpty())
            throw new IllegalArgumentException("Table name cannot be empty");
        if (mapToEntity == null)
            throw new IllegalArgumentException("MapToEntity converter required");
    }
}
