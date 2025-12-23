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
    int insert(OutboxEntity entity);

    void update(OutboxEntity entity);

    List<OutboxEntity> findUnsent(int limit);

    int markSending(Long id);

    int markSent(Long id);

    int markFailed(Long id);
}
