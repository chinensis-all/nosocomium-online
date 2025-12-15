
package com.mayanshe.nosocomiumonline.application.dynamic;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mayanshe.nosocomiumonline.application.dynamic.config.CrudConfig;
import com.mayanshe.nosocomiumonline.application.dynamic.repository.DynamicRepository;
import com.mayanshe.nosocomiumonline.application.messaging.EventPublisher;
import com.mayanshe.nosocomiumonline.shared.base.BaseQuery;
import com.mayanshe.nosocomiumonline.shared.contract.ICreateCommandToEntity;
import com.mayanshe.nosocomiumonline.shared.event.IntegrationEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 动态 CRUD 服务。
 * <p>
 * 基于注册的配置提供通用的 CRUD 操作。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicCrudService {
    private final DynamicRepository dynamicRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final EventPublisher eventPublisher;
    private final StringRedisTemplate redisTemplate;

    private final Cache<String, Object> cache = CacheUtil.newTimedCache(10 * 60 * 1000);

    private final Map<String, CrudConfig<?>> configMap = new ConcurrentHashMap<>(); // 配置映射

    /**
     * 注册 CRUD 配置。
     * 
     * @param config CRUD 配置
     */
    public void register(CrudConfig<?> config) {
        config.validate();
        configMap.put(config.getName(), config);
        log.info("Registered Dynamic CRUD for entity: {}", config.getEntityType().getSimpleName());
    }

    /**
     * 获取实体类型的配置。
     */
    private <E> CrudConfig<E> getConfig(String configName) {
        CrudConfig<?> config = configMap.get(configName.toLowerCase());
        if (config == null) {
            throw new IllegalArgumentException("No CRUD configuration found for: " + configName);
        }

        return (CrudConfig<E>) config;
    }

    public <DTO> DTO find(String configName, Long id) {
        configName = configName.toLowerCase();
        CrudConfig<DTO> config = getConfig(configName);

        // 查询缓存
        if (config.isEnableDetailCache()) {
            String cacheKey = String.format("%s:id=%d", StrUtil.upperFirst(StrUtil.toCamelCase(config.getName())), id);

        }
    }

    /**
     * 根据 ID 查找模型
     *
     * @param configName 配置名称
     * @param id         实体ID
     * @return 模型数据映射
     */
    public Map<String, Object>  findRaw(String configName, Long id) {
        CrudConfig<?> config = getConfig(configName);
        String cacheKey = config.isEnableFindCache() ? String.format("%s:id=%d", StrUtil.upperFirst(StrUtil.toCamelCase(config.getName())), id) : null;

        if (cacheKey != null) {
            Map<String, Object> cached = getFromCache(cacheKey);
            if (cached != null)
                return cached;
        }

        Map<String, Object> result = dynamicRepository.findById(config.getTableName(), config.getPkName(), id);

        if (cacheKey != null && result != null) {
            putCache(cacheKey, result, config.getFindCacheTtl());
        }
        return result;
    }

    // ===================================================================================
    // Create
    // ===================================================================================

    @Transactional
    public Long create(String configName, Map<String, Object> data) {
        CrudConfig<?> config = getConfig(configName);
        Long id = dynamicRepository.insert(config.getTableName(), config.getPkName(), data);

        // Event
        if (config.isPublishEvents()) {
            publishEvent(config, "CREATED", String.valueOf(id), data);
        }
        return id;
    }

    @Transactional
    public <C> Long create(String configName, C command) {
        CrudConfig<Object> config = getConfig(configName);
        @SuppressWarnings("unchecked")
        ICreateCommandToEntity<C, Object> converter = (ICreateCommandToEntity<C, Object>) config
                .getCreateCommandToEntity();
        Assert.notNull(converter, "No CreateCommand converter registered for " + configName);

        Object entity = converter.toEntity(command);
        Map<String, Object> map = convertEntityToMap(entity);
        Long id = dynamicRepository.insert(config.getTableName(), config.getPkName(), map);

        if (config.isPublishEvents()) {
            publishEvent(config, "CREATED", String.valueOf(id), map);
        }
        return id;
    }

    // ===================================================================================
    // Modify
    // ===================================================================================

    @Transactional
    public void modify(String configName, Long id, Map<String, Object> data) {
        CrudConfig<?> config = getConfig(configName);
        dynamicRepository.update(config.getTableName(), config.getPkName(), id, data);

        // Cache Eviction
        if (config.isEnableFindCache()) {
            evictCache(String.format("%s:id=%d", config.getName(), id));
        }

        // Event
        if (config.isPublishEvents()) {
            publishEvent(config, "MODIFIED", String.valueOf(id), data);
        }
    }

    @Transactional
    public <M> void modify(String configName, M command) {
        throw new UnsupportedOperationException(
                "Modify with Command object requires explicitID or Refection. Not fully implemented yet.");
    }

    // ===================================================================================
    // Destroy
    // ===================================================================================

    @Transactional
    public void destroy(String configName, Long id) {
        CrudConfig<?> config = getConfig(configName);

        if (config.isSoftDelete()) {
            dynamicRepository.softDelete(config.getTableName(), config.getPkName(), id);
        } else {
            dynamicRepository.delete(config.getTableName(), config.getPkName(), id);
        }

        // Cache Eviction
        if (config.isEnableFindCache()) {
            evictCache(String.format("%s:id=%d", config.getName(), id));
        }

        // Event
        if (config.isPublishEvents()) {
            publishEvent(config, "DELETED", String.valueOf(id), Map.of("id", id));
        }
    }

    // ===================================================================================
    // Search & Pagination (Cached)
    // ===================================================================================

    public List<Map<String, Object>> search(String configName, BaseQuery query, int limit, int offset) {
        return search(configName, query.toMap(), limit, offset);
    }

    public List<Map<String, Object>> search(String configName, Map<String, Object> criteria, int limit,
            int offset) {
        CrudConfig<?> config = getConfig(configName);

        String listCacheKey = null;
        if (config.isEnableListCache()) {
            String hash = generateCriteriaHash(criteria, limit, offset);
            listCacheKey = String.format("Paginate:%s:criteria=%s", config.getName(), hash);
            List<Map<String, Object>> cached = getListFromCache(listCacheKey);
            if (cached != null)
                return cached;
        }

        List<Map<String, Object>> result = dynamicRepository.search(config.getTableName(), criteria, limit, offset);

        if (listCacheKey != null && result != null) {
            // Note: List cache is short-lived and not evicted on Modify because criteria
            // keys are hard to track.
            // Rely on TTL (60s).
            putCache(listCacheKey, result, config.getListCacheTtl());
        }
        return result;
    }

    public List<Map<String, Object>> paginate(String configName, Map<String, Object> criteria, int page,
            int size) {
        int offset = (page - 1) * size;
        return search(configName, criteria, size, offset);
    }

    public List<Map<String, Object>> keysetPaginate(String configName, Map<String, Object> criteria,
            String keysetColumn, Object keysetValue, int limit) {
        CrudConfig<?> config = getConfig(configName);

        // Similar cache strategy for Keyset
        String listCacheKey = null;
        if (config.isEnableListCache()) {
            String hash = generateCriteriaHash(criteria, keysetColumn, keysetValue, limit);
            listCacheKey = String.format("Paginate:%s:criteria=%s", config.getName(), hash);
            List<Map<String, Object>> cached = getListFromCache(listCacheKey);
            if (cached != null)
                return cached;
        }

        String col = keysetColumn != null ? keysetColumn : config.getPkName();
        List<Map<String, Object>> result = dynamicRepository.keysetSearch(config.getTableName(), criteria, col,
                keysetValue, limit);

        if (listCacheKey != null && result != null) {
            putCache(listCacheKey, result, config.getListCacheTtl());
        }
        return result;
    }

    // ===================================================================================
    // Utils & Helpers
    // ===================================================================================

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertEntityToMap(Object entity) {
        return objectMapper.convertValue(entity, Map.class);
    }

    private void publishEvent(CrudConfig<?> config, String eventType, String aggregateId,
            Map<String, Object> payloadMap) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payloadMap);
            DynamicIntegrationEvent event = DynamicIntegrationEvent.builder()
                    .aggregateType(config.getName())
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(payloadJson)
                    .occurredAt(LocalDateTime.now())
                    .build();
            eventPublisher.publish(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payload for event publishing", e);
        }
    }

    private <T> T getFromCache(String key, Class<T> type) {
        Object value = cache.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new ClassCastException(
                    "Cache value type mismatch. key=" + key +
                            ", expected=" + type.getName() +
                            ", actual=" + value.getClass().getName()
            );
        }
        return (T) value;
    }

    private List<Map<String, Object>> getListFromCache(String key) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null)
            return null;
        try {
            return objectMapper.readValue(json, List.class);
        } catch (Exception e) {
            log.warn("Cache list deserialization failed for key: {}", key, e);
            return null;
        }
    }

    private void putCache(String key, Object value, long ttlSeconds) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Cache put failed for key: {}", key, e);
        }
    }

    private void evictCache(String key) {
        redisTemplate.delete(key);
    }

    private String generateCriteriaHash(Object... components) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            StringBuilder sb = new StringBuilder();
            for (Object c : components) {
                sb.append(String.valueOf(c));
            }
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(System.currentTimeMillis()); // Fallback
        }
    }

    @Builder
    @Getter
    private static class DynamicIntegrationEvent implements IntegrationEvent {
        private final String eventType;
        private final String aggregateId;
        private final String aggregateType;
        private final String payload;
        private final LocalDateTime occurredAt;

        @Override
        public String eventType() {
            return eventType;
        }

        @Override
        public String aggregateId() {
            return aggregateId;
        }

        @Override
        public String aggregateType() {
            return aggregateType;
        }

        public String payload() {
            return payload;
        }

        @Override
        public LocalDateTime occurredAt() {
            return occurredAt;
        }
    }
}
