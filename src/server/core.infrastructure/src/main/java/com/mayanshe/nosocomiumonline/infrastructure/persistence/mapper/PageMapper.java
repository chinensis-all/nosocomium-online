package com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PageMapper: 分页查询通用Mapper接口
 *
 * @param <T> 实体类型
 * @author zhangxihai
 */
public interface PageMapper<T> {
    long count(Map<String, Object> criteria);

    List<T> search(@Param("criteria") Map<String, Object> criteria, @Param("offset") long offset, @Param("limit") int limit);

    List<T> searchWithSort(@Param("criteria") Map<String, Object> criteria, @Param("offset") Set<String> sorts, @Param("offset") long offset, @Param("limit") int limit);
}
