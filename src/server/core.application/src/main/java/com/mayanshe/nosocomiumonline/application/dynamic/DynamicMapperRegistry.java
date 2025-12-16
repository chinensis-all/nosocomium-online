package com.mayanshe.nosocomiumonline.application.dynamic;

import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DynamicMapperRegistry: 动态映射注册表。
 */
@Component
public class DynamicMapperRegistry {
    private final Map<Type, Object> mapperMap = new ConcurrentHashMap<>();

    public void register(Type type, Object mapper) {
        mapperMap.put(type, mapper);
    }

    @SuppressWarnings("unchecked")
    public <M> M getMapper(Type entityType) {
        Object mapper = mapperMap.get(entityType);
        if (mapper == null) {
            throw new IllegalStateException("No mapper registered for entityType: " + entityType);
        }
        return (M) mapper;
    }
}
