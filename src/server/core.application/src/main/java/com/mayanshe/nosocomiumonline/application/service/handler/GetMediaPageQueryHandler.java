package com.mayanshe.nosocomiumonline.application.service.handler;

import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.application.dto.query.GetMeidaPageQuery;
import com.mayanshe.nosocomiumonline.application.service.repository.MediaQueryRepository;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryHandler;
import com.mayanshe.nosocomiumonline.shared.valueobject.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * GetMediaPageQueryHandler: 处理获取媒体分页查询
 *
 * @author zhagnxihai
 */
@Component
@RequiredArgsConstructor
public class GetMediaPageQueryHandler implements QueryHandler<GetMeidaPageQuery, Pagination<MediaDto>> {

    private final MediaQueryRepository mediaQueryRepository;

    @Override
    public Pagination<MediaDto> handle(GetMeidaPageQuery query) {
        return mediaQueryRepository.queryPage(query.toMap(), query.page(), query.pageSize());
    }
}
