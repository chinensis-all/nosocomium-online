package com.mayanshe.nosocomiumonline.domain.media.event;

import com.mayanshe.nosocomiumonline.domain.media.value.MediaType;
import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import com.mayanshe.nosocomiumonline.shared.storage.BucketType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * MediaUploadedEvent: 媒体上传领域事件
 */
@Getter
@ToString
@RequiredArgsConstructor
public class MediaUploadedEvent implements DomainEvent {
    private final Long id;

    private final String md5;

    private final String objectKey;

    private final BucketType bucketType;

    private final MediaType mediaType;

    private final LocalDateTime occurredAt = LocalDateTime.now();

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }
}
