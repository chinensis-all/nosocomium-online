package com.mayanshe.nosocomiumonline.task.job;

import com.mayanshe.nosocomiumonline.domain.kernel.eventing.Outbox;
import com.mayanshe.nosocomiumonline.domain.kernel.eventing.OutboxRepository;
import com.mayanshe.nosocomiumonline.infrastructure.event.MessageBus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * OutboxDispatchJob: Outbox 派发任务
 *
 * @author zhangxihai
 */
@Component
@RequiredArgsConstructor
public class OutboxDispatchJob {
    private final OutboxRepository outboxRepository;
    private final MessageBus messageBus;

    @Scheduled(fixedDelay = 1000)
    public void dispatch() {

        List<Outbox> events = outboxRepository.findUnset(100);

        for (Outbox event : events) {

            // 乐观锁：抢占发送权
            if (!outboxRepository.markSending(event.getId())) {
                continue;
            }

            try {
                messageBus.publish(event);
                outboxRepository.markSent(event.getId());
            } catch (Exception ex) {
                outboxRepository.markFailed(event.getId());
            }
        }
    }
}
