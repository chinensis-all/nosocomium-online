package com.mayanshe.nosocomiumonline.task.job;

import com.mayanshe.nosocomiumonline.infrastructure.persistence.entity.OutboxEntity;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper.OutboxMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPollerTask {

    private final OutboxMapper outboxMapper;

    @Scheduled(fixedDelay = 2000)
    public void processOutbox() {
        // 1. Fetch NEW events
        List<OutboxEntity> events = outboxMapper.selectNewEvents(50);
        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} new events to process", events.size());

        for (OutboxEntity event : events) {
            try {
                // 2. Publish (Simulation)
                log.info("Publishing event: {} (ID: {})", event.getEventType(), event.getId());
                // In real world: producer.send(topic, payload)

                // 3. Mark as SENT
                event.setStatus("SENT");
                event.setSentAt(LocalDateTime.now());
                outboxMapper.update(event);

            } catch (Exception e) {
                log.error("Failed to process event: " + event.getId(), e);
                // Retry logic: increment retry_count, check max retries, etc.
                event.setRetryCount(event.getRetryCount() + 1);
                outboxMapper.update(event);
            }
        }
    }
}
