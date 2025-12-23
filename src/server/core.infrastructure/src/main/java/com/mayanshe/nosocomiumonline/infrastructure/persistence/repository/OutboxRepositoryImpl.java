package com.mayanshe.nosocomiumonline.infrastructure.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayanshe.nosocomiumonline.application.messaging.EventPublisher;
import com.mayanshe.nosocomiumonline.domain.kernel.eventing.Outbox;
import com.mayanshe.nosocomiumonline.domain.kernel.eventing.OutboxRepository;
import com.mayanshe.nosocomiumonline.infrastructure.event.EventEnvelopeFactory;
import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import com.mayanshe.nosocomiumonline.shared.event.IntegrationEvent;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.entity.OutboxEntity;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper.OutboxMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OutboxRepositoryImpl: 集成事件外发存储库实现
 *
 * @author zhangxihai
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxMapper outboxMapper;

    private final ObjectMapper objectMapper;

    private final EventEnvelopeFactory eventEnvelopeFactory;

    @Override
    public void save(IntegrationEvent event) {
        OutboxEntity entity = eventEnvelopeFactory.from(event);
        if (outboxMapper.insert(entity) <= 0) {
            throw new RuntimeException("Failed to save integration event to outbox");
        }
    }

    @Override
    public void save(DomainEvent event, String aggregateType, String aggregateId) {
        OutboxEntity entity = eventEnvelopeFactory.from(event, aggregateType, aggregateId);
        if (outboxMapper.insert(entity) <= 0) {
            throw new RuntimeException("Failed to save domain event to outbox");
        }
    }

    @Override
    public void save(Iterable<? extends DomainEvent> events, String aggregateType, String aggregateId) {
        for (DomainEvent event : events) {
            save(event, aggregateType, aggregateId);
        }
    }

    @Override
    public List<Outbox> findUnset(int limit) {
        ModelMapper modelMapper = new ModelMapper();
        List<OutboxEntity> entities = outboxMapper.findUnsent(limit);
        return entities.stream().map(entity -> modelMapper.map(entity, Outbox.class)).toList();
    }

    @Override
    public boolean markSending(Long id) {
        return outboxMapper.markSending(id) > 0;
    }

    @Override
    public boolean markSent(Long id) {
        return outboxMapper.markSent(id) > 0;
    }

    @Override
    public boolean markFailed(Long id) {
        return outboxMapper.markFailed(id) > 0;
    }
}
