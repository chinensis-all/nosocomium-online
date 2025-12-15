package com.mayanshe.nosocomiumonline.shared.contract;

/**
 * Contract for converting an Entity to a DTO.
 *
 * @param <E> The Entity type.
 * @param <D> The DTO type.
 */
public interface IEntityToDto<E, D> {
    /**
     * Converts an entity to a DTO.
     *
     * @param entity The entity.
     * @return The DTO.
     */
    D toDto(E entity);
}
