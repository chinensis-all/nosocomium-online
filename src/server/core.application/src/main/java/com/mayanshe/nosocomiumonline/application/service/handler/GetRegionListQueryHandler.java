package com.mayanshe.nosocomiumonline.application.service.handler;

import com.mayanshe.nosocomiumonline.application.dto.RegionDto;
import com.mayanshe.nosocomiumonline.application.dto.query.GetRegionListQuery;
import com.mayanshe.nosocomiumonline.application.service.repository.RegionQueryRepository;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GetRegionListQueryHandler: 获取行政区划列表查询处理器。
 *
 * @author zhangxihai
 */
@Component
@RequiredArgsConstructor
public class GetRegionListQueryHandler implements QueryHandler<GetRegionListQuery, List<RegionDto>> {

    private final RegionQueryRepository regionQueryRepository;

    @Override
    public List<RegionDto> handle(GetRegionListQuery query) {
        return regionQueryRepository.search(query.parentId(), query.keywords());
    }
}
