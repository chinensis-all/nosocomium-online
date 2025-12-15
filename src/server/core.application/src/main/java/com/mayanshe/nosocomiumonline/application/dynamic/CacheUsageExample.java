package com.mayanshe.nosocomiumonline.application.dynamic;

import com.mayanshe.nosocomiumonline.application.dynamic.config.CrudConfig;
import com.mayanshe.nosocomiumonline.shared.contract.ICache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存使用示例
 * 演示如何注入 ICache 并使用 remember 方法
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CacheUsageExample {

    private final ICache cache;

    /**
     * 演示获取缓存配置
     */
    public CrudConfig<?> getCachedConfig(String configName) {
        // 定义缓存 Key
        String key = "config:" + configName;

        // 定义 TTL (例如 10 分钟)
        long ttl = 600;

        // 使用 remember 方法：
        // 1. 尝试从缓存获取 Config
        // 2. 如果未命中，执行 loader (模拟从数据库加载)
        // 3. 将结果写入缓存并返回
        return cache.remember(key, ttl, CrudConfig.class, () -> {
            log.info("Loading config from DB for: {}", configName);
            // 模拟加载逻辑
            // return repository.findConfig(configName);
            return CrudConfig.builder()
                    .name(configName)
                    .tableName("example_table")
                    .build();
        });
    }

    /**
     * 演示移除缓存
     */
    public void paramsUpdate(String configName) {
        String key = "config:" + configName;
        // 配置更新后移除缓存
        cache.remove(key);
        log.info("Cache evicted for: {}", configName);
    }
}
