package com.mayanshe.nosocomiumonline.domain.kernel.eventing;

import java.time.LocalDateTime;

/**
 * Domain Event Interface.
 * <p>
 * Rules:
 * 1. Must be immutable.
 * 2. No framework annotations.
 * 3. Represents a fact that has happened.
 */
public interface DomainEvent {

    /**
     * When the event occurred.
     */
    LocalDateTime occurredAt();
}
