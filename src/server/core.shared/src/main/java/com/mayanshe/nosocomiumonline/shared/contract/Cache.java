package com.mayanshe.nosocomiumonline.shared.contract;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * 缓存接口抽象
 */
public interface Cache {
    /**
     * 移除缓存
     *
     * @param key 缓存 Key
     */
    void remove(String key);

    /**
     * 获取缓存，若不存在则调用 loader 加载并写入缓存
     *
     * @param key        缓存 Key
     * @param ttlSeconds 过期时间（秒）
     * @param type       数据类型
     * @param loader     数据加载器
     * @param <T>       数据类型泛型
     * @return 数据
     */
    <T> T remember(String key, long ttlSeconds, Type type, Supplier<T> loader);
}
