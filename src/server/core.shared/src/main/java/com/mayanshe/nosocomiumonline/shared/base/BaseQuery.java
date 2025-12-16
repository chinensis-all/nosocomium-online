package com.mayanshe.nosocomiumonline.shared.base;

import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Base class for Dynamic Query objects.
 */
@SuperBuilder(toBuilder = true)
public abstract class BaseQuery {

    /**
     * Converts the query object to a Map for database processing.
     *
     * @return Map representation of the query criteria.
     */
    public abstract Map<String, Object> toMap();

    // Workaround for SuperBuilder requiring a constructor, although abstract class
    public BaseQuery() {
    }
}
