package com.mayanshe.nosocomiumonline.domain.media.event;

import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * MediaDeletedEvent: 媒体删除领域事件
 */
@Getter
@ToString
@RequiredArgsConstructor
public class MediaDeletedEvent implements DomainEvent {
    private final Long id;

    private final String md5;

    private final LocalDateTime occurredAt = LocalDateTime.now();

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }
}
