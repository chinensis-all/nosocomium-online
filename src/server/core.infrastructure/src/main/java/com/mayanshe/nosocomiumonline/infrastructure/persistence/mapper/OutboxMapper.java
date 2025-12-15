package com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper;

import com.mayanshe.nosocomiumonline.infrastructure.persistence.entity.OutboxEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * OutboxMapper: 用于操作事件外发表的MyBatis映射器
 */
@Mapper
public interface OutboxMapper {

    @Insert("INSERT INTO event_outbox (aggregate_type, aggregate_id, event_type, payload, status, retry_count, occurred_at, created_at) "
            +
            "VALUES (#{aggregateType}, #{aggregateId}, #{eventType}, #{payload}, #{status}, #{retryCount}, #{occurredAt}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OutboxEntity entity);

    @Select("SELECT * FROM event_outbox WHERE status = 'NEW' ORDER BY created_at ASC LIMIT #{limit}")
    List<OutboxEntity> selectNewEvents(int limit);

    @Insert("UPDATE event_outbox SET status = #{status}, sent_at = #{sentAt}, retry_count = #{retryCount} WHERE id = #{id}")
    void update(OutboxEntity entity);
}
