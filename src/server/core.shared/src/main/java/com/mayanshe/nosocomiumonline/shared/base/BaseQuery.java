package com.mayanshe.nosocomiumonline.shared.base;

import java.util.Map;

/**
 * Base class for Dynamic Query objects.
 */
public abstract class BaseQuery {

    /**
     * Converts the query object to a Map for database processing.
     *
     * @return Map representation of the query criteria.
     */
    public abstract Map<String, Object> toMap();
}
