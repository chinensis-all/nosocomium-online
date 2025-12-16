package com.mayanshe.nosocomiumonline.domain.media;

import java.util.Optional;

/**
 * 媒体资源库接口。
 */
public interface MediaRepository {
    /**
     * 保存媒体信息。
     *
     * @param media 媒体对象
     * @return 保存后的 ID
     */
    Long save(Media media);

    /**
     * 更新媒体信息（不更新存储相关字段）。
     *
     * @param media 媒体对象
     */
    void updateInfo(Media media);

    /**
     * 根据 ID 获取媒体信息。
     *
     * @param id 媒体 ID
     * @return 媒体对象 Opional
     */
    Optional<Media> findById(Long id);

    /**
     * 根据 MD5 获取媒体信息（用于去重）。
     *
     * @param md5 文件 MD5
     * @return 媒体对象 Optional
     */
    Optional<Media> findByMd5(String md5);

    /**
     * 根据 ID 删除媒体信息。
     *
     * @param id 媒体 ID
     */
    void deleteById(Long id);
}
