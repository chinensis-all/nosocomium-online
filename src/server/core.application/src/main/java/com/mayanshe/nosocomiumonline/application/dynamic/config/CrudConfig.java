package com.mayanshe.nosocomiumonline.application.dynamic.config;

import com.mayanshe.nosocomiumonline.shared.contract.ICreateCommandToEntity;
import com.mayanshe.nosocomiumonline.shared.contract.IEntityToDto;
import com.mayanshe.nosocomiumonline.shared.contract.IMapToEntity;
import com.mayanshe.nosocomiumonline.shared.contract.IModifyCommandToEntity;
import lombok.Builder;
import lombok.Getter;

/**
 * Configuration for Dynamic CRUD Service.
 *
 * @param <E> Entity Type
 */
@Getter
@Builder
public class CrudConfig<E> {
    private final Class<E> entityType;
    private final String tableName;
    @Builder.Default
    private final String pkName = "id";

    // Converters
    private final IMapToEntity<E> mapToEntity;
    private final IEntityToDto<E, ?> entityToDto;
    private final ICreateCommandToEntity<?, E> createCommandToEntity;
    private final IModifyCommandToEntity<?, E> modifyCommandToEntity;

    // Feature Flags
    @Builder.Default
    private final boolean isSoftDelete = false;
    @Builder.Default
    private final boolean publishEvents = true;

    // Cache Settings
    @Builder.Default
    private final boolean enableFindCache = false;
    @Builder.Default
    private final long findCacheTtl = 600; // Seconds

    @Builder.Default
    private final boolean enableListCache = true;
    @Builder.Default
    private final long listCacheTtl = 60; // Seconds

    public void validate() {
        if (entityType == null)
            throw new IllegalArgumentException("Entity type cannot be null");
        if (tableName == null || tableName.isEmpty())
            throw new IllegalArgumentException("Table name cannot be empty");
        if (mapToEntity == null)
            throw new IllegalArgumentException("MapToEntity converter required");
    }
}
