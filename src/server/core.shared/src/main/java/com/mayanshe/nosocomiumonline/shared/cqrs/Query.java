package com.mayanshe.nosocomiumonline.shared.cqrs;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.RecordComponent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Query: 查询接口，所有查询对象必须实现此接口。
 *
 * @author zhangxihai
 */
public interface Query {
    Map<Class<?>, Map<String, MethodHandle>> CACHE = new ConcurrentHashMap<>();

    default Map<String, Object> toMap() {
        Class<?> clazz = this.getClass();

        if (!clazz.isRecord()) {
            throw new UnsupportedOperationException("Query must be a record: " + clazz.getName());
        }

        Map<String, MethodHandle> accessors = CACHE.computeIfAbsent(clazz, this::extractAccessors);

        Map<String, Object> map = new LinkedHashMap<>(accessors.size());

        try {
            for (Map.Entry<String, MethodHandle> entry : accessors.entrySet()) {
                Object value = entry.getValue().invoke(this);
                map.put(entry.getKey(), value);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to convert Query to Map", e);
        }

        return map;
    }

    private Map<String, MethodHandle> extractAccessors(Class<?> c) {
        Map<String, MethodHandle> map = new LinkedHashMap<>();
        MethodHandles.Lookup lookup = MethodHandles.publicLookup();
        for (RecordComponent rc : c.getRecordComponents()) {
            try {
                MethodHandle mh = lookup.unreflect(rc.getAccessor());
                map.put(rc.getName(), mh);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        return map;
    }
}
