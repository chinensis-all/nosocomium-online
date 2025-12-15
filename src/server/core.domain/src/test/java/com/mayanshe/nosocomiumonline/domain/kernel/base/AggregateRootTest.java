package com.mayanshe.nosocomiumonline.domain.kernel.base;

import com.mayanshe.nosocomiumonline.domain.kernel.eventing.DomainEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AggregateRootTest {

    static class TestAggregate extends AggregateRoot {
        void doSomething() {
            registerEvent(new DomainEvent() {
                @Override
                public LocalDateTime occurredAt() {
                    return LocalDateTime.now();
                }
            });
        }
    }

    @Test
    void testEventRegistration() {
        TestAggregate aggregate = new TestAggregate();
        aggregate.doSomething();

        assertEquals(1, aggregate.getDomainEvents().size());

        aggregate.clearDomainEvents();
        assertEquals(0, aggregate.getDomainEvents().size());
    }
}
