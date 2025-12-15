package com.mayanshe.nosocomiumonline.shared.contract;

import java.util.Map;

/**
 * 将 Map 转换为 Entity 的契约。
 *
 * @param <E> 实体类型。
 */
public interface IMapToEntity<E> {
    /**
     * 将 Map 转换为实体。
     *
     * @param map 包含数据的 Map。
     * @return 实体。
     */
    E toEntity(Map<String, Object> map);
}
