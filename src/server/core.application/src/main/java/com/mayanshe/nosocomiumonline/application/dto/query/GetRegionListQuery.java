package com.mayanshe.nosocomiumonline.application.dto.query;

import com.mayanshe.nosocomiumonline.shared.cqrs.Query;

/**
 * 获取行政区划列表查询。
 *
 * @param parentId 上级行政编码
 * @param keywords 关键词
 */
public record GetRegionListQuery(
    Long parentId,
    String keywords
) implements Query {}
