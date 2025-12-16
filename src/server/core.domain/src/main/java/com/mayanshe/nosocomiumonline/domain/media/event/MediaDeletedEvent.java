package com.mayanshe.nosocomiumonline.domain.media.event;

import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
@RequiredArgsConstructor
public class MediaDeletedEvent implements DomainEvent {
    private final Long id;
    private final Instant occurredOn = Instant.now();

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
