package com.mayanshe.nosocomiumonline.shared.contract;

import java.util.Map;

/**
 * Contract for converting a Map to an Entity.
 *
 * @param <E> The Entity type.
 */
public interface IMapToEntity<E> {
    /**
     * Converts a map to an entity.
     *
     * @param map The map containing data.
     * @return The entity.
     */
    E toEntity(Map<String, Object> map);
}
