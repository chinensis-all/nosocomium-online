package com.mayanshe.nosocomiumonline.shared.contract;

/**
 * 将 Entity 转换为 DTO 的契约。
 *
 * @param <E> 实体类型。
 * @param <D> DTO 类型。
 */
public interface EntityToDto<E, D> {
    /**
     * 将实体转换为 DTO。
     *
     * @param entity 实体。
     * @return DTO。
     */
    D toDto(E entity);
}
