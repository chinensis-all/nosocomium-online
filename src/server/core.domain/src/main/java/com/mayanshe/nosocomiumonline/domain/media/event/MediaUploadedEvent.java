package com.mayanshe.nosocomiumonline.domain.media.event;

import com.mayanshe.nosocomiumonline.domain.media.MediaType;
import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import com.mayanshe.nosocomiumonline.shared.storage.BucketType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
@RequiredArgsConstructor
public class MediaUploadedEvent implements DomainEvent {
    private final Long id;
    private final String md5;
    private final String objectKey;
    private final BucketType bucketType;
    private final MediaType mediaType;
    private final String url;
    private final Instant occurredOn = Instant.now();

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
