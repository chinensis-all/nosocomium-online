package com.mayanshe.nosocomiumonline.application.service.repository;

import com.mayanshe.nosocomiumonline.shared.valueobject.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 查询仓库接口
 * @param <Dto> 数据传输对象类型
 */
public interface QueryRepository<Dto> {
    Optional<Dto> queryById(Long id);

    List<KeyValue> queryOptions(Map<String, Object> criteria);

    List<Dto> queryList(Map<String, Object> criteria);

    Pagination<Dto> queryPage(Map<String, Object> criteria, int page, int size);

    KeysetPagination<Dto> queryKeysetPage(Map<String, Object> criteria, Long lastId, int size, Ascending ascending, Direction direction);
}
