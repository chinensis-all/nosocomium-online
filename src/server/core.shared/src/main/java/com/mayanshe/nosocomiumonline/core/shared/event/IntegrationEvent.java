package com.mayanshe.nosocomiumonline.core.shared.event;

import java.time.LocalDateTime;

/**
 * Integration Event Interface.
 * <p>
 * Used for cross-module or cross-service communication.
 * Must be serializable to JSON.
 */
public interface IntegrationEvent {

    /**
     * Type of the event (e.g. "PatientRegistered").
     */
    String eventType();

    /**
     * Type of the Aggregate Root (e.g. "Patient").
     */
    String aggregateType();

    /**
     * ID of the Aggregate Root related to this event.
     */
    String aggregateId();

    /**
     * When the event occurred.
     */
    LocalDateTime occurredAt();
}
