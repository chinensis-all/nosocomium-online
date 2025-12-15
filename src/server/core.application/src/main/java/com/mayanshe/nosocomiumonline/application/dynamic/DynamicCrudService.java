package com.mayanshe.nosocomiumonline.application.dynamic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mayanshe.nosocomiumonline.application.dynamic.config.CrudConfig;
import com.mayanshe.nosocomiumonline.application.dynamic.repository.DynamicRepository;
import com.mayanshe.nosocomiumonline.application.messaging.EventPublisher;
import com.mayanshe.nosocomiumonline.shared.base.BaseQuery;
import com.mayanshe.nosocomiumonline.shared.contract.ICreateCommandToEntity;
import com.mayanshe.nosocomiumonline.shared.contract.IModifyCommandToEntity;
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
 * Dynamic CRUD Service.
 * <p>
 * Provides generic CRUD operations based on registered configurations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicCrudService {

    private final DynamicRepository dynamicRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final EventPublisher eventPublisher;
    private final StringRedisTemplate redisTemplate;

    private final Map<Class<?>, CrudConfig<?>> configMap = new ConcurrentHashMap<>();

    /**
     * Registers a configuration.
     *
     * @param config The configuration to register.
     */
    public void register(CrudConfig<?> config) {
        config.validate();
        configMap.put(config.getEntityType(), config);
        log.info("Registered Dynamic CRUD for entity: {}", config.getEntityType().getSimpleName());
    }

    /**
     * Retrieves configuration for an entity type.
     */
    @SuppressWarnings("unchecked")
    private <E> CrudConfig<E> getConfig(Class<E> entityType) {
        CrudConfig<?> config = configMap.get(entityType);
        Assert.notNull(config, "No configuration found for entity: " + entityType.getSimpleName());
        return (CrudConfig<E>) config;
    }

    // ===================================================================================
    // Find (Cached)
    // ===================================================================================

    public <E> Map<String, Object> findRaw(Class<E> entityType, Long id) {
        CrudConfig<E> config = getConfig(entityType);
        String cacheKey = config.isEnableFindCache() ? String.format("%s:id=%d", entityType.getSimpleName(), id) : null;

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
    public <E> Long create(Class<E> entityType, Map<String, Object> data) {
        CrudConfig<E> config = getConfig(entityType);
        Long id = dynamicRepository.insert(config.getTableName(), config.getPkName(), data);

        // Event
        if (config.isPublishEvents()) {
            publishEvent(config, "CREATED", String.valueOf(id), data);
        }
        return id;
    }

    @Transactional
    public <C, E> Long create(Class<E> entityType, C command) {
        CrudConfig<E> config = getConfig(entityType);
        @SuppressWarnings("unchecked")
        ICreateCommandToEntity<C, E> converter = (ICreateCommandToEntity<C, E>) config.getCreateCommandToEntity();
        Assert.notNull(converter, "No CreateCommand converter registered for " + entityType.getSimpleName());

        E entity = converter.toEntity(command);
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
    public <E> void modify(Class<E> entityType, Long id, Map<String, Object> data) {
        CrudConfig<E> config = getConfig(entityType);
        dynamicRepository.update(config.getTableName(), config.getPkName(), id, data);

        // Cache Eviction
        if (config.isEnableFindCache()) {
            evictCache(String.format("%s:id=%d", entityType.getSimpleName(), id));
        }

        // Event
        if (config.isPublishEvents()) {
            publishEvent(config, "MODIFIED", String.valueOf(id), data);
        }
    }

    @Transactional
    public <M, E> void modify(Class<E> entityType, M command) {
        throw new UnsupportedOperationException(
                "Modify with Command object requires explicitID or Refection. Not fully implemented yet.");
    }

    // ===================================================================================
    // Destroy (New)
    // ===================================================================================

    @Transactional
    public <E> void destroy(Class<E> entityType, Long id) {
        CrudConfig<E> config = getConfig(entityType);

        if (config.isSoftDelete()) {
            dynamicRepository.softDelete(config.getTableName(), config.getPkName(), id);
        } else {
            dynamicRepository.delete(config.getTableName(), config.getPkName(), id);
        }

        // Cache Eviction
        if (config.isEnableFindCache()) {
            evictCache(String.format("%s:id=%d", entityType.getSimpleName(), id));
        }

        // Event
        if (config.isPublishEvents()) {
            publishEvent(config, "DELETED", String.valueOf(id), Map.of("id", id));
        }
    }

    // ===================================================================================
    // Search & Pagination (Cached)
    // ===================================================================================

    public <E> List<Map<String, Object>> search(Class<E> entityType, BaseQuery query, int limit, int offset) {
        return search(entityType, query.toMap(), limit, offset);
    }

    public <E> List<Map<String, Object>> search(Class<E> entityType, Map<String, Object> criteria, int limit,
            int offset) {
        CrudConfig<E> config = getConfig(entityType);

        String listCacheKey = null;
        if (config.isEnableListCache()) {
            String hash = generateCriteriaHash(criteria, limit, offset);
            listCacheKey = String.format("Paginate:%s:criteria=%s", entityType.getSimpleName(), hash);
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

    public <E> List<Map<String, Object>> paginate(Class<E> entityType, Map<String, Object> criteria, int page,
            int size) {
        int offset = (page - 1) * size;
        return search(entityType, criteria, size, offset);
    }

    public <E> List<Map<String, Object>> keysetPaginate(Class<E> entityType, Map<String, Object> criteria,
            String keysetColumn, Object keysetValue, int limit) {
        CrudConfig<E> config = getConfig(entityType);

        // Similar cache strategy for Keyset
        String listCacheKey = null;
        if (config.isEnableListCache()) {
            String hash = generateCriteriaHash(criteria, keysetColumn, keysetValue, limit);
            listCacheKey = String.format("Paginate:%s:criteria=%s", entityType.getSimpleName(), hash);
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
                    .aggregateType(config.getEntityType().getSimpleName())
                    .aggregateId(aggregateId)
                    .eventType(eventType) // e.g., CREATED, MODIFIED, DELETED
                    .payload(payloadJson)
                    .occurredAt(LocalDateTime.now())
                    .build();
            eventPublisher.publish(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payload for event publishing", e);
        }
    }

    // --- Cache Helpers ---

    private Map<String, Object> getFromCache(String key) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null)
            return null;
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            log.warn("Cache deserialization failed for key: {}", key, e);
            return null;
        }
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

        @Override
        public String payload() {
            return payload;
        }

        @Override
        public LocalDateTime occurredAt() {
            return occurredAt;
        }
    }
}
