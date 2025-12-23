package com.mayanshe.nosocomiumonline.application.dynamic;

import com.mayanshe.nosocomiumonline.shared.contract.CreateCommandToEntity;
import com.mayanshe.nosocomiumonline.shared.contract.EntityToDto;
import com.mayanshe.nosocomiumonline.shared.contract.MapToEntity;
import com.mayanshe.nosocomiumonline.shared.contract.ModifyCommandToEntity;
import lombok.Builder;
import lombok.Getter;

/**
 * 动态CRUD服务配置
 *
 * @param <E> Entity类型
 */
@Getter
@Builder
public class CrudConfig<E, D> {
    private final String name;                                             // 配置名称

    private final String title;                                            // 配置标题,用于错误输出

    private final Class<E> entityType;                                     // Entity类型

    private final Class<D> dtoType;                                        // DTO类型

    private final EntityToDto<E, D> entityToDto;                          // Entity到DTO的转换器

    private final MapToEntity<E> mapToEntity;                             // Map到Entity的转换器

    private final CreateCommandToEntity<?, E> createCommandToEntity;      // 创建Command到Entity的转换器

    private final ModifyCommandToEntity<?, E> modifyCommandToEntity;      // 修改Command到Entity的转换器

    @Builder.Default
    private final boolean publishEvents = true;                            // 是否发布事件

    @Builder.Default
    private final boolean isSoftDelete = false;                            // 是否启用软删除

    @Builder.Default
    private final boolean enableDetailCache = false;                       // 是否启用Detail缓存

    @Builder.Default
    private final long detailCacheTTL = 300;                               // Detail缓存TTL，单位秒

    @Builder.Default
    private final boolean enableSearchCache = false;                       // 是否启用Search缓存

    @Builder.Default
    private final long searchCacheTTL = 60;                                // Search缓存TTL，单位秒

    @Builder.Default
    private final boolean enablePaginateCache = false;                     // 是否启用Paginate缓存

    @Builder.Default
    private final long paginateCacheTTL = 60;                              // Paginate缓存TTL，单位秒

    @Builder.Default
    private final boolean enableKetsetPaginateCache = false;               // 是否启用KeysetPaginate缓存

    @Builder.Default
    private final long keysetPaginateCacheTTL = 60;                        // KeysetPaginate缓存TTL，单位秒

    @Builder.Default
    private final boolean enableKeyValueCache = false;                      // 是否启用KeyValue缓存

    @Builder.Default
    private final long keyValueCacheTTL = 300;                             // KeyValue缓存TTL，单位秒

    public void validate() {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name cannot be null or empty");

        if (title == null || title.isEmpty()) throw new IllegalArgumentException("Title cannot be null or empty");

        if (entityType == null) throw new IllegalArgumentException("Entity type cannot be null");

        if (dtoType == null) throw new IllegalArgumentException("DTO type cannot be null");
    }
}
