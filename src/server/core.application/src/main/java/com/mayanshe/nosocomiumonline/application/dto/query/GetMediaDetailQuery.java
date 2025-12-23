package com.mayanshe.nosocomiumonline.application.dto.query;

import com.mayanshe.nosocomiumonline.shared.cqrs.Query;

/**
 * 获取媒体详情查询。
 *
 * @param id 媒体ID
 */
public record GetMediaDetailQuery(Long id) implements Query {}
