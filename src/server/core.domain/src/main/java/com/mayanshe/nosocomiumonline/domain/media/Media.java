package com.mayanshe.nosocomiumonline.domain.media;

import com.mayanshe.nosocomiumonline.shared.storage.BucketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 媒体实体。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    /**
     * ID
     */
    private Long id;

    /**
     * 存储桶类型
     */
    private BucketType bucketType;

    /**
     * 媒体类型
     */
    private MediaType mediaType;

    /**
     * 文件 MD5
     */
    private String md5;

    /**
     * 对象键（存储路径）
     */
    private String objectKey;

    /**
     * 文件大小（字节）
     */
    private Long sizeBytes;

    /**
     * Content Type
     */
    private String contentType;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
