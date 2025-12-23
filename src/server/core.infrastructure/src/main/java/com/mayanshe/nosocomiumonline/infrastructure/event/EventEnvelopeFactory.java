package com.mayanshe.nosocomiumonline.infrastructure.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.entity.OutboxEntity;
import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import com.mayanshe.nosocomiumonline.shared.event.IntegrationEvent;
import org.springframework.stereotype.Component;

/**
 * EventEnvelopeFactory: 事件封装工厂
 *
 * @author zhangxihai
 */
@Component
public class EventEnvelopeFactory {
    private final ObjectMapper objectMapper;

    public EventEnvelopeFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OutboxEntity from(DomainEvent event, String aggregateType, String aggregateId) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            return OutboxEntity.builder()
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(event.getClass().getSimpleName())
                    .payload(payload)
                    .status("new")
                    .retryCount(0)
                    .occurredAt(event.occurredAt())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize domain event", e);
        }
    }

    public OutboxEntity from(IntegrationEvent event) {
        return OutboxEntity.builder()
                .aggregateType(event.aggregateType())
                .aggregateId(event.aggregateId())
                .eventType(event.eventType())
                .payload(event.payload())
                .status("sent")
                .retryCount(0)
                .occurredAt(event.occurredAt())
                .build();
    }
}
