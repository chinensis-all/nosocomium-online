package com.mayanshe.nosocomiumonline.application.dto.query;

import com.mayanshe.nosocomiumonline.domain.media.value.MediaType;
import com.mayanshe.nosocomiumonline.shared.cqrs.Query;
import com.mayanshe.nosocomiumonline.shared.storage.BucketType;

import java.util.Map;

/**
 * 获取媒体分页查询。
 *
 * @param bucketType 存储桶类型
 * @param mediaType  媒体类型
 * @param keywords   关键词
 * @param page       页码
 * @param pageSize   每页大小
 */
public record GetMeidaPageQuery(
        String bucketType,
        String mediaType,
        String keywords,
        int page,
        int pageSize
) implements Query {}
