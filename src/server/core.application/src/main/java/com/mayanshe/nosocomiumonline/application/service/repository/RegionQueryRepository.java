package com.mayanshe.nosocomiumonline.application.service.repository;

import com.mayanshe.nosocomiumonline.application.dto.RegionDto;

import java.util.List;

/**
 * RegionQueryRepository: 行政区划查询仓储接口。
 *
 * @author zhangxihai
 */
public interface RegionQueryRepository {
    List<RegionDto> search(Long parentId, String keywords);
}
