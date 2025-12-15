package com.mayanshe.nosocomiumonline.core.domain.repository;

import com.mayanshe.nosocomiumonline.core.shared.event.IntegrationEvent;

/**
 * Repository interface for saving Integration Events to the Outbox.
 * <p>
 * Implementation should be in Infrastructure layer.
 */
public interface OutboxRepository {

    /**
     * Save an integration event to the outbox.
     *
     * @param event The event to save.
     */
    void save(IntegrationEvent event);
}
