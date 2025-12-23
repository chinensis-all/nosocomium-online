package com.mayanshe.nosocomiumonline.domain.kernel.base;

import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
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
}
