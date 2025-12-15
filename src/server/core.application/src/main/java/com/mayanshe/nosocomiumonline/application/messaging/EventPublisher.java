package com.mayanshe.nosocomiumonline.application.messaging;

import com.mayanshe.nosocomiumonline.domain.kernel.eventing.OutboxRepository;
import com.mayanshe.nosocomiumonline.shared.event.IntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * EventPublisher: 集成事件发布器
 * <p>
 * 负责将集成事件发布到 Outbox，以实现可靠的事件传递。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventPublisher {

    private final OutboxRepository outboxRepository;

    /**
     * 将集成事件发布到 Outbox。
     * <p>
     * 此方法必须在现有事务中调用（MANDATORY），以确保与业务逻辑的原子性。
     *
     * @param event 要发布的事件。
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(IntegrationEvent event) {
        if (event == null) {
            log.warn("Attempted to publish null event");
            return;
        }
        outboxRepository.save(event);
        log.debug("Event published to outbox: {} / {}", event.eventType(), event.aggregateId());
    }

    /**
     * 批量将集成事件发布到 Outbox。
     *
     * @param events 要发布的事件集合。
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(java.util.Collection<IntegrationEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        for (IntegrationEvent event : events) {
            this.publish(event);
        }
    }
}
