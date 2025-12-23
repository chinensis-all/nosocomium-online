package com.mayanshe.nosocomiumonline.infrastructure.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayanshe.nosocomiumonline.domain.kernel.eventing.Outbox;
import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * SpringDomainEventPublisher: 使用 Spring 的 ApplicationEventPublisher 发布领域事件
 *
 * @author zhangxihai
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements MessageBus {

    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(Outbox outboxEvent) {
        DomainEvent domainEvent = deserialize(outboxEvent);
        publisher.publishEvent(domainEvent);
    }

    private DomainEvent deserialize(Outbox outboxEvent) {
        try {
            Class<?> eventClass = Class.forName(outboxEvent.getEventType());

            return (DomainEvent) objectMapper.readValue(outboxEvent.getPayload(), eventClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize domain event", e);
        }
    }
}
