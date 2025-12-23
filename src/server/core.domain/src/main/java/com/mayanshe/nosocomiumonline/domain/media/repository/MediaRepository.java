package com.mayanshe.nosocomiumonline.domain.media.repository;

import com.mayanshe.nosocomiumonline.domain.kernel.base.DomainRepository;
import com.mayanshe.nosocomiumonline.domain.media.model.Media;

/**
 * 媒体仓库接口。
 */
public interface MediaRepository extends DomainRepository<Media> {
    Media loadByMd5(String md5);
}
