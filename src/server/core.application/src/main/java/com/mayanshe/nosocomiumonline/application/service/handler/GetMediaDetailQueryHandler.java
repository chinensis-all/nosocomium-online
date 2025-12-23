package com.mayanshe.nosocomiumonline.application.service.handler;

import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.application.dto.query.GetMediaDetailQuery;
import com.mayanshe.nosocomiumonline.application.service.repository.MediaQueryRepository;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryHandler;
import com.mayanshe.nosocomiumonline.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * GetMediaDetailQueryHandler: 处理获取媒体详情查询
 *
 * @author zhangxihai
 */
@Component
@RequiredArgsConstructor
public class GetMediaDetailQueryHandler implements QueryHandler<GetMediaDetailQuery, MediaDto> {

    private final MediaQueryRepository mediaQueryRepository;

    @Override
    public MediaDto handle(GetMediaDetailQuery query) {
        return mediaQueryRepository.queryById(query.id())
                .orElseThrow(() -> new NotFoundException("媒体资源不存在: " + query.id()));
    }
}
