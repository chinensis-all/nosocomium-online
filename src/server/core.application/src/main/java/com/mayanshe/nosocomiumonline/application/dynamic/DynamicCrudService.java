package com.mayanshe.nosocomiumonline.application.dynamic;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayanshe.nosocomiumonline.application.messaging.EventPublisher;
import com.mayanshe.nosocomiumonline.shared.base.BaseKeysetQuery;
import com.mayanshe.nosocomiumonline.shared.base.BasePageQuery;
import com.mayanshe.nosocomiumonline.shared.base.BaseQuery;
import com.mayanshe.nosocomiumonline.shared.contract.*;
import com.mayanshe.nosocomiumonline.shared.event.IntegrationEvent;
import com.mayanshe.nosocomiumonline.shared.exception.BadRequestException;
import com.mayanshe.nosocomiumonline.shared.valueobject.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 动态 CRUD 服务。
 * <p>
 * 基于注册的配置提供通用的 CRUD 操作。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicCrudService {

    private static final String EVENT_CREATED = "Created";
    private static final String EVENT_MODIFIED = "Modified";
    private static final String EVENT_DELETED = "Deleted";
    private static final String EVENT_SOFT_DELETED = "SoftDeleted";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final DynamicRepository dynamicRepository;

    private final ModelMapper modelMapper;

    private final EventPublisher eventPublisher;

    private final Cache cache;

    private final DynamicCrudConfigRegistry configRegistry;

    /**
     * 根据 ID 查找模型并映射为 DTO
     *
     * @param configName 配置名称
     * @param id         实体ID
     * @param <Dto>      DTO 类型
     * @return DTO 对象
     */
    public <Dto extends IdAccessor> Dto find(String configName, Long id) {
        String name = configName.toLowerCase();
        CrudConfig<Object, Dto> config = getConfig(name);

        if (config.isEnableDetailCache()) {
            String key = getDetailCacheKey(name, id);
            Type dtoType = config.getDtoType();

            // Explicitly cast to Class<Dto> if possible or use Type
            return cache.remember(key, config.getDetailCacheTTL(), dtoType, () -> this.find(id, config));
        }

        return this.find(id, config);
    }

    /**
     * 根据 ID 查找模型并映射为 DTO
     *
     * @param id     实体ID
     * @param config CRUD 配置
     * @param <Dto>  DTO 类型
     * @return DTO 对象
     */
    public <Dto extends IdAccessor> Dto find(Long id, CrudConfig<Object, Dto> config) {
        Object entity = dynamicRepository.findById(id, config.getEntityType());

        if (entity == null) {
            return null;
        }

        return mapEntityToDto(entity, config);
    }

    /***
     * 创建新实体
     *
     * @param configName 配置名称
     * @param data       实体数据映射
     * @return 新实体的ID
     */
    @Transactional
    public long create(String configName, Map<String, Object> data) {
        long id = IdGenerator.nextId();
        data.put("id", id);

        var config = getConfig(configName);
        Object entity;

        if (config.getMapToEntity() == null) {
            entity = modelMapper.map(data, config.getEntityType());
        } else {
            entity = config.getMapToEntity().toEntity(data);
        }

        if (entity == null) {
            throw new BadRequestException("添加" + config.getTitle() + "失败");
        }

        if (1 > dynamicRepository.insert(entity, config.getEntityType())) {
            throw new BadRequestException("添加" + config.getTitle() + "失败");
        }

        // 伪事件发布
        if (config.isPublishEvents()) {
            publishEvent(config, EVENT_CREATED, String.valueOf(id), entity);
        }

        return id;
    }

    /**
     * 创建新实体
     *
     * @param configName 配置名称
     * @param command    创建命令对象
     * @param <Cmd>      创建命令类型
     * @return 新实体的ID
     */
    @Transactional
    public <Cmd extends DynamicCommand> long create(String configName, Cmd command) {
        long id = IdGenerator.nextId();
        command.setId(id);

        var config = getConfig(configName);
        Object entity;

        if (config.getCreateCommandToEntity() == null) {
            entity = modelMapper.map(command, config.getEntityType());
        } else {
            @SuppressWarnings("unchecked") CreateCommandToEntity<Cmd, Object> converter = (CreateCommandToEntity<Cmd, Object>) config.getCreateCommandToEntity();
            entity = converter.toEntity(command);
        }

        if (entity == null) {
            throw new BadRequestException("添加" + config.getTitle() + "失败");
        }

        if (1 > dynamicRepository.insert(entity, config.getEntityType())) {
            throw new BadRequestException("添加" + config.getTitle() + "失败");
        }

        // 伪事件发布
        if (config.isPublishEvents()) {
            publishEvent(config, EVENT_CREATED, String.valueOf(id), entity);
        }

        return id;
    }

    /***
     * 修改实体
     *
     * @param configName 配置名称
     * @param data       实体数据映射，必须包含 "id" 键
     */
    @Transactional
    public void modify(String configName, Map<String, Object> data) {
        if (!data.containsKey("id")) {
            throw new BadRequestException("请求参数错误，缺少 ID 字段");
        }

        var config = getConfig(configName);
        Long id = Long.valueOf(data.get("id").toString());
        Object entity = dynamicRepository.findById(id, config.getEntityType());
        if (entity == null) {
            throw new BadRequestException(config.getName() + " 未找到，ID: " + id);
        }

        Map<String, Object> safeData = new HashMap<>(data);
        safeData.remove("id");

        if (config.getMapToEntity() == null) {
            modelMapper.map(safeData, entity);
        } else {
            config.getMapToEntity().applyToEntity(data, entity);
        }

        if (1 > dynamicRepository.update(entity, config.getEntityType())) {
            throw new BadRequestException("修改" + config.getTitle() + "失败");
        }

        // 伪事件发布
        if (config.isPublishEvents()) {
            publishEvent(config, EVENT_MODIFIED, String.valueOf(id), entity);
        }
    }

    /***
     * 修改实体
     *
     * @param configName 配置名称
     * @param command    修改命令对象，必须包含 ID
     * @param <Cmd>      修改命令类型
     */
    @Transactional
    public <Cmd extends DynamicCommand> void modify(String configName, Cmd command) {
        var config = getConfig(configName);

        Long id = command.getId();
        Object entity = dynamicRepository.findById(id, config.getEntityType());
        if (entity == null) {
            throw new BadRequestException(config.getName() + " 未找到，ID: " + id);
        }

        if (config.getModifyCommandToEntity() == null) {
            modelMapper.map(command, entity);
        } else {
            @SuppressWarnings("unchecked") ModifyCommandToEntity<Cmd, Object> converter = (ModifyCommandToEntity<Cmd, Object>) config.getModifyCommandToEntity();
            converter.updateEntity(command, entity);
        }

        if (1 > dynamicRepository.update(entity, config.getEntityType())) {
            throw new BadRequestException("修改" + config.getTitle() + "失败");
        }

        // 伪事件发布
        if (config.isPublishEvents()) {
            publishEvent(config, EVENT_MODIFIED, String.valueOf(id), entity);
        }
    }

    /***
     * 删除实体
     *
     * @param configName 配置名称
     * @param id         实体ID
     */
    @Transactional
    public void destroy(String configName, Long id) {
        var config = getConfig(configName);

        if (config.isSoftDelete()) {
            dynamicRepository.softDeleteById(id, config.getEntityType());
        } else {
            dynamicRepository.deleteById(id, config.getEntityType());
        }

        if (config.isEnableDetailCache()) {
            String key = getDetailCacheKey(configName.toLowerCase(), id);
            cache.remove(key);
        }

        // 伪事件发布
        if (config.isPublishEvents()) {
            publishEvent(config, config.isSoftDelete() ? EVENT_SOFT_DELETED : EVENT_DELETED, String.valueOf(id), Map.of("id", id));
        }
    }

    /***
     * 删除实体
     *
     * @param configName 配置名称
     * @param command    删除命令对象，必须包含 ID
     * @param <Cmd>>     删除命令类型
     */
    @Transactional
    public <Cmd extends DynamicCommand> void destroy(String configName, Cmd command) {
        Long id = command.getId();
        destroy(configName, id);
    }

    /**
     * 搜索实体列表
     *
     * @param configName 配置名称
     * @param query      查询对象
     * @param limit      限制数量
     * @param offset     偏移数量
     * @param <Dto>      DTO 类型
     * @return DTO 列表
     */
    public <Dto extends IdAccessor> List<Dto> search(String configName, BaseQuery query, int limit, int offset) {
        return search(configName, query.toMap(), limit, offset);
    }

    /***
     * 搜索实体列表
     *
     * @param configName 配置名称
     * @param criteria   查询条件映射
     * @param limit      限制数量
     * @param offset     偏移数量
     * @param <Dto>      DTO 类型
     * @return DTO 列表
     */
    public <Dto extends IdAccessor> List<Dto> search(String configName, Map<String, Object> criteria, int limit, int offset) {
        CrudConfig<Object, Dto> config = getConfig(configName);

        List<Dto> res;

        if (config.isEnableSearchCache()) {
            String hash = generateCriteriaHash(criteria, limit, offset);
            String searchCacheKey = String.format("Search:%s:criteria=%s", config.getName(), hash);
            // Assuming cache returns List<Dto> correctly
            res = cache.remember(searchCacheKey, config.getSearchCacheTTL(), List.class, () -> this.search(criteria, limit, offset, config));
        } else {
            res = this.search(criteria, limit, offset, config);
        }

        return res == null ? List.of() : res;
    }

    /**
     * 搜索实体列表
     *
     * @param criteria 查询条件映射
     * @param limit    限制数量
     * @param offset   偏移数量
     * @param config   CRUD 配置
     * @param <Dto>    DTO 类型
     * @return DTO 列表
     */
    private <Dto extends IdAccessor> List<Dto> search(Map<String, Object> criteria, int limit, int offset, CrudConfig<Object, Dto> config) {
        List<Object> entities = dynamicRepository.search(criteria, limit, offset, config.getEntityType());
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return mapEntitiesToDtos(entities, config);
    }

    /**
     * 分页查询实体列表
     *
     * @param configName 配置名称
     * @param query      查询对象
     * @param <Dto>      DTO 类型
     * @return 分页结果
     */
    public <Dto extends IdAccessor> Pagination<Dto> paginate(String configName, BasePageQuery query) {
        return paginate(configName, query.toMap(), query.getPage(), query.getPageSize());
    }

    /**
     * 分页查询实体列表
     *
     * @param configName 配置名称
     * @param criteria   查询条件映射
     * @param page       页码
     * @param size       每页数量
     * @param <Dto>      DTO 类型
     * @return 分页结果
     */
    public <Dto extends IdAccessor> Pagination<Dto> paginate(String configName, Map<String, Object> criteria, int page, int size) {
        CrudConfig<Object, Dto> config = getConfig(configName);

        if (config.isEnablePaginateCache()) {
            String hash = generateCriteriaHash(criteria);
            String paginateCacheKey = String.format("Paginate:%s:criteria=%s,page=%d,size=%d", StrUtil.upperFirst(StrUtil.toCamelCase(config.getName())), hash, page, size);
            return cache.remember(paginateCacheKey, config.getPaginateCacheTTL(), List.class, () -> this.paginate(criteria, page, size, config));
        }

        return this.paginate(criteria, page, size, config);
    }

    /**
     * 分页查询实体列表
     *
     * @param criteria 查询条件映射
     * @param page     页码
     * @param size     每页数量
     * @param config   CRUD 配置
     * @param <Dto>    DTO 类型
     * @return 分页结果
     */
    public <Dto extends IdAccessor> Pagination<Dto> paginate(Map<String, Object> criteria, int page, int size, CrudConfig<Object, Dto> config) {
        long total = dynamicRepository.count(criteria, config.getEntityType());
        if (total == 0) {
            return Pagination.of(page, size, 0, List.of());
        }

        List<Dto> items = search(criteria, size, (page - 1) * size, config);
        return Pagination.of(page, size, total, items);
    }

    /***
     * 键集分页查询实体列表
     *
     * @param configName 配置名称
     * @param query      查询对象
     * @param <Dto>      DTO 类型
     * @return 键集分页结果
     */
    public <Dto extends IdAccessor> KeysetPagination<Dto> keysetPaginate(String configName, BaseKeysetQuery query) {
        return keysetPaginate(configName, query.toMap(), Long.valueOf(query.getCursor().toString()), query.getLimit(), query.isAscending(), query.isNext());
    }

    /***
     * 键集分页查询实体列表
     *
     * @param configName 配置名称
     * @param criteria   查询条件映射
     * @param id         键集ID
     * @param limit      限制数量
     * @param ascending  是否升序
     * @param isNext     是否为下一页
     * @param <Dto>      DTO 类型
     * @return 键集分页结果
     */
    public <Dto extends IdAccessor> KeysetPagination<Dto> keysetPaginate(String configName, Map<String, Object> criteria, Long id, int limit, boolean ascending, boolean isNext) {
        CrudConfig<Object, Dto> config = getConfig(configName);

        if (config.isEnableKetsetPaginateCache()) {
            String hash = generateCriteriaHash(criteria, id, limit, ascending, isNext);
            String keysetPaginateCacheKey = String.format("KeysetPaginate:%s:criteria=%s", StrUtil.upperFirst(StrUtil.toCamelCase(config.getName())), hash);
            return cache.remember(keysetPaginateCacheKey, config.getKeysetPaginateCacheTTL(), KeysetPagination.class, () -> this.keysetPaginate(criteria, id, limit, ascending, isNext, config));
        }

        return this.keysetPaginate(criteria, id, limit, ascending, isNext, config);
    }

    /***
     * 键集分页查询实体列表
     *
     * @param criteria  查询条件映射
     * @param id        键集ID
     * @param limit     限制数量
     * @param ascending 是否升序
     * @param isNext    是否为下一页
     * @param config    CRUD 配置
     * @param <Dto>     DTO 类型
     * @return 键集分页结果
     */
    private <Dto extends IdAccessor> KeysetPagination<Dto> keysetPaginate(Map<String, Object> criteria, Long id, int limit, boolean ascending, boolean isNext, CrudConfig<Object, Dto> config) {
        List<Object> entities = dynamicRepository.keysetSearch(criteria, ascending ? Ascending.ASC : Ascending.DESC, isNext ? Direction.NEXT : Direction.PREV, id, limit, config.getEntityType());

        if (entities == null || entities.isEmpty()) {
            return KeysetPagination.empty();
        }

        List<Dto> items = mapEntitiesToDtos(entities, config);

        return KeysetPagination.of(items, items.get(items.size() - 1).getId().toString(), items.get(0).getId().toString());
    }

    /***
     * 获取键值对列表(不建议使用)
     *
     * @param configName 配置名称
     * @param criteria   查询条件映射
     * @param limit      限制数量
     * @return 键值对列表
     */
    public List<KeyValue> fetchKeyValues(String configName, Map<String, Object> criteria, int limit) {
        CrudConfig<?, ?> config = getConfig(configName);
        if (KeyValue.class.isAssignableFrom(config.getEntityType())) {
            throw new IllegalArgumentException("DTO type cannot be KeyValue for fetchKeyValues operation.");
        }

        if (config.isEnableKeyValueCache()) {
            String hash = generateCriteriaHash(criteria);
            String keyValueCacheKey = String.format("KeyValue:%s:criteria=%s,limit=%d", StrUtil.upperFirst(StrUtil.toCamelCase(config.getName())), hash, limit);
            return cache.remember(keyValueCacheKey, config.getKeyValueCacheTTL(), List.class, () -> this.fetchKeyValues(configName, criteria, limit, config));
        }

        return this.fetchKeyValues(configName, criteria, limit, config);
    }

    /***
     * 获取键值对列表
     *
     * @param configName 配置名称
     * @param criteria   查询条件映射
     * @param limit      限制数量
     * @param config     CRUD 配置
     * @return 键值对列表
     */
    public List<KeyValue> fetchKeyValues(String configName, Map<String, Object> criteria, int limit, CrudConfig<?, ?> config) {
        List<Object> entities = dynamicRepository.search(criteria, limit, 0, config.getEntityType());
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return entities.stream().map((e) -> {
            KeyValueAccessor accessor = (KeyValueAccessor) e;
            return new KeyValue(accessor.getKey(), accessor.getValue());
        }).toList();
    }

    /**
     * 获取 CRUD 配置
     *
     * @param configName 配置名称
     * @param <Dto>      DTO 类型
     * @return CRUD 配置对象
     */
    private <Dto> CrudConfig<Object, Dto> getConfig(String configName) {
        CrudConfig<Object, Dto> config = configRegistry.get(configName.toLowerCase());
        if (config == null) {
            throw new IllegalArgumentException("No CRUD configuration found for: " + configName);
        }

        return config;
    }

    /**
     * 生成详情缓存键
     *
     * @param configName 配置名称
     * @param id         实体ID
     * @return 缓存键字符串
     */
    private String getDetailCacheKey(String configName, Long id) {
        return String.format("%s:id=%d", StrUtil.upperFirst(StrUtil.toCamelCase(configName)), id);
    }

    /***
     * 发布集成事件
     *
     * @param config      CRUD 配置
     * @param eventType   事件类型
     * @param aggregateId 聚合ID
     * @param payloadMap  负载数据映射
     */
    private void publishEvent(CrudConfig<?, ?> config, String eventType, String aggregateId, Object payloadMap) {
        try {
            String payloadJson = OBJECT_MAPPER.writeValueAsString(payloadMap);
            DynamicIntegrationEvent event = DynamicIntegrationEvent.builder().aggregateType(config.getName()).aggregateId(aggregateId).eventType(eventType).payload(payloadJson).occurredAt(LocalDateTime.now()).build();
            eventPublisher.publish(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payload for event publishing. Aggregate: {}, ID: {}", config.getName(), aggregateId, e);
        }
    }

    private String generateCriteriaHash(Object... components) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            StringBuilder sb = new StringBuilder();
            for (Object c : components) {
                sb.append(c);
            }
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // MD5 is standard, this should not happen in standard JVM environment
            log.warn("MD5 algorithm not found, reverting to simple hash strategy", e);
            return String.valueOf(java.util.Arrays.deepHashCode(components));
        }
    }

    private <Dto extends IdAccessor> List<Dto> mapEntitiesToDtos(List<Object> entities, CrudConfig<Object, Dto> config) {
        return entities.stream().map(entity -> mapEntityToDto(entity, config)).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private <Dto extends IdAccessor> Dto mapEntityToDto(Object entity, CrudConfig<Object, Dto> config) {
        // Po与Dto类型相同，直接返回
        if (config.getEntityType().equals(config.getDtoType())) {
            return (Dto) entity;
        }

        EntityToDto<Object, Dto> entityToDto = config.getEntityToDto();

        // 使用转换器进行转换
        if (entityToDto != null) {
            return entityToDto.toDto(entity);
        }

        // 如果未指定转换器， 直接用ModelMapper转换
        return modelMapper.map(entity, config.getDtoType());
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
