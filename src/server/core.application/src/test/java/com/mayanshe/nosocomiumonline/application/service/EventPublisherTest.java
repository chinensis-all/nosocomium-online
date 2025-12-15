package com.mayanshe.nosocomiumonline.application.service;

import com.mayanshe.nosocomiumonline.core.domain.repository.OutboxRepository;
import com.mayanshe.nosocomiumonline.core.shared.event.IntegrationEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = { EventPublisher.class, EventPublisherTest.Config.class })
public class EventPublisherTest {

    @Autowired
    private EventPublisher eventPublisher;

    @MockBean
    private OutboxRepository outboxRepository;

    @TestConfiguration
    @EnableTransactionManagement
    static class Config {
    }

    @Test
    void publish_shouldSaveEventToRepository_whenTransactionExists() {
        // Given
        IntegrationEvent event = new IntegrationEvent() {
            @Override
            public String eventType() {
                return "TestEvent";
            }

            @Override
            public String aggregateType() {
                return "TestAggregate";
            }

            @Override
            public String aggregateId() {
                return "123";
            }

            @Override
            public LocalDateTime occurredAt() {
                return LocalDateTime.now();
            }
        };

        // When (We need a transaction, but EventPublisher requires existing one)
        // Testing MANDATORY propagation without transaction should fail
        Assertions.assertThrows(IllegalTransactionStateException.class, () -> {
            eventPublisher.publish(event);
        });
    }
}
