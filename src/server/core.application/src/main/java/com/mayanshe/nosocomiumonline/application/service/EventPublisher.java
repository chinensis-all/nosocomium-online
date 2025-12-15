package com.mayanshe.nosocomiumonline.application.service;

import com.mayanshe.nosocomiumonline.core.domain.repository.OutboxRepository;
import com.mayanshe.nosocomiumonline.core.shared.event.IntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventPublisher {

    private final OutboxRepository outboxRepository;

    /**
     * Publishes an integration event to the Outbox.
     * <p>
     * This method must be called within an existing transaction (MANDATORY) to
     * ensure atomicity with business logic.
     *
     * @param event The event to publish.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(IntegrationEvent event) {
        if (event == null) {
            log.warn("Attempted to publish null event");
            return;
        }
        outboxRepository.save(event);
        log.debug("Event published to outbox: {}/{}", event.eventType(), event.aggregateId());
    }
}
