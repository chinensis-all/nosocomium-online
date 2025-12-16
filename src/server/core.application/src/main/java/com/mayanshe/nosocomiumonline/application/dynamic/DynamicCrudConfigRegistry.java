package com.mayanshe.nosocomiumonline.application.dynamic;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * DynamicCrudConfigRegistry: 动态CRUD配置注册表
 */
@Component
public class DynamicCrudConfigRegistry {
    private final Map<String, CrudConfig<?, ?>> configs = new ConcurrentHashMap<>();

    /**
     * 注册Crud配置
     *
     * @param name   配置名称
     * @param config Crud配置对象
     */
    public void register(String name, CrudConfig<?, ?> config) {
        config.validate();
        configs.put(name.toLowerCase(), config);
    }

    /**
     * 获取Crud配置
     *
     * @param name 配置名称
     * @param <E>  Entity类型
     * @param <D>  Dto类型
     * @return Crud配置对象
     */
    @SuppressWarnings("unchecked")
    public <E, D> CrudConfig<E, D> get(String name) {
        CrudConfig<?, ?> config = configs.get(name.toLowerCase());
        if (config == null) {
            throw new IllegalArgumentException("CrudConfig not found: " + name);
        }
        return (CrudConfig<E, D>) config;
    }
}
