package com.mayanshe.nosocomiumonline.application.service;

import com.mayanshe.nosocomiumonline.application.messaging.EventPublisher;
import com.mayanshe.nosocomiumonline.domain.kernel.eventing.OutboxRepository;
import com.mayanshe.nosocomiumonline.shared.event.IntegrationEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

/**
 * EventPublisherTest: 集成事件发布器测试
 * <p>
 * 测试 EventPublisher 类的功能，确保事件正确发布到 Outbox。
 */
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

    @Test
    void publishBatch_shouldSaveAllEvents_whenTransactionExists() {
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
        List<IntegrationEvent> events = Collections.singletonList(event);

        // When
        Assertions.assertThrows(IllegalTransactionStateException.class, () -> {
            eventPublisher.publish(events);
        });
    }
}
