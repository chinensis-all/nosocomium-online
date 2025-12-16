package com.mayanshe.nosocomiumonline.shared.event;

import java.io.Serializable;
import java.time.Instant;

/**
 * Marker interface for Domain Events.
 */
public interface DomainEvent extends Serializable {
    /**
     * Occurred time of the event.
     */
    Instant occurredOn();
}
