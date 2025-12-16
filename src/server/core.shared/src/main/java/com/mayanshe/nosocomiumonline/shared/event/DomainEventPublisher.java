package com.mayanshe.nosocomiumonline.shared.event;

/**
 * Interface for publishing domain events.
 * Implementation will typically delegate to Spring ApplicationEventPublisher or
 * a message bus.
 */
public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
