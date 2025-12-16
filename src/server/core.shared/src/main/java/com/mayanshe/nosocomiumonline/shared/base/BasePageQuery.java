package com.mayanshe.nosocomiumonline.shared.base;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * PageQuery: 分页查询对象
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public abstract class BasePageQuery extends BaseQuery {
    private int page;

    private int pageSize;

    /**
     * Converts the query object to a Map for database processing.
     *
     * @return Map representation of the query criteria.
     */
    public abstract Map<String, Object> toMap();
}
