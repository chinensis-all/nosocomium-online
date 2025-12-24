package com.mayanshe.nosocomiumonline.infrastructure.persistence.repository;

import com.mayanshe.nosocomiumonline.application.dto.RegionDto;
import com.mayanshe.nosocomiumonline.application.service.repository.RegionQueryRepository;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper.RegionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RegionRepositoryImpl: 行政区划仓储实现类。
 *
 * @author zhangxihai
 */
@Repository
@RequiredArgsConstructor
public class RegionRepositoryImpl implements RegionQueryRepository {
    private final RegionMapper regionMapper;

    @Override
    public List<RegionDto> search(Long parentId, String keywords)
    {
        return regionMapper.search(parentId, keywords);
    }
}
