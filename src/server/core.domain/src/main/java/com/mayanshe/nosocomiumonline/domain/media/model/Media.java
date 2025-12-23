package com.mayanshe.nosocomiumonline.domain.media.model;

import com.mayanshe.nosocomiumonline.domain.kernel.base.AggregateRoot;
import com.mayanshe.nosocomiumonline.domain.media.event.MediaDeletedEvent;
import com.mayanshe.nosocomiumonline.domain.media.event.MediaInfoModifiedEvent;
import com.mayanshe.nosocomiumonline.domain.media.event.MediaUploadedEvent;
import com.mayanshe.nosocomiumonline.domain.media.value.MediaType;
import com.mayanshe.nosocomiumonline.shared.storage.BucketType;
import com.mayanshe.nosocomiumonline.shared.valueobject.AggregateId;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 媒体实体。
 */
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Media extends AggregateRoot {
    private AggregateId id;

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 上传媒体文件
     */
    public void upload()
    {
        if (this.getId() == null || this.getId().getId() == null || this.getId().getId() <= 0) {
            throw new IllegalStateException("Media dos not has an ID. Please add.");
        }

        MediaUploadedEvent event = new MediaUploadedEvent(
                this.getId().getId(),
                this.md5,
                this.objectKey,
                this.bucketType,
                this.mediaType
        );
        this.registerEvent(event);
    }

    /**
     * 修改媒体信息
     *
     * @param title       媒体资源标题
     * @param description 媒体资源描述
     */
    public void modifyInfo(String title, String description) {
        this.title = title;
        this.description = description;

        MediaInfoModifiedEvent event= new MediaInfoModifiedEvent(
                this.getId().getId(),
                this.md5,
                title,
                description
        );
        this.registerEvent(event);
    }

    /**
     * 删除媒体文件
     */
    public void delete() {
        if (this.getId() == null || this.getId().getId() == null || this.getId().getId() <= 0) {
            throw new IllegalStateException("Media dos not has an ID. Please add.");
        }

        MediaDeletedEvent event = new MediaDeletedEvent(
                this.getId().getId(),
                this.md5
        );
        this.registerEvent(event);
    }
}
