package com.mayanshe.nosocomiumonline.infrastructure.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayanshe.nosocomiumonline.domain.kernel.eventing.OutboxRepository;
import com.mayanshe.nosocomiumonline.shared.event.IntegrationEvent;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.entity.OutboxEntity;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper.OutboxMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * OutboxRepositoryImpl: 集成事件外发存储库实现
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void save(IntegrationEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEntity entity = OutboxEntity.builder()
                    .aggregateType(event.aggregateType())
                    .aggregateId(event.aggregateId())
                    .eventType(event.eventType())
                    .payload(payload)
                    .status("NEW")
                    .retryCount(0)
                    .occurredAt(event.occurredAt())
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxMapper.insert(entity);
            log.info("Saved event to outbox: {}/{}", event.eventType(), event.aggregateId());

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event payload", e);
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
