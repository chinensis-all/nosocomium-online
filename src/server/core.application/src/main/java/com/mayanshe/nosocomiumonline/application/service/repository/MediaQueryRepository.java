package com.mayanshe.nosocomiumonline.application.service.repository;

import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.shared.valueobject.Pagination;

import java.util.Map;
import java.util.Optional;

/**
 * MediaQueryRepository: 媒体查询仓库接口
 *
 * @author zhangxihai
 */
public interface MediaQueryRepository {
    Optional<MediaDto> queryById(Long id);

    Pagination<MediaDto> queryPage(Map<String, Object> criteria, int page, int size);
}
