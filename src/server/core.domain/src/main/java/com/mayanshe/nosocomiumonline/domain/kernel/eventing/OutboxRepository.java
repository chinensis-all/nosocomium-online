package com.mayanshe.nosocomiumonline.domain.kernel.eventing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import com.mayanshe.nosocomiumonline.shared.event.IntegrationEvent;

import java.util.List;


/**
 * OutboxRepository: 集成事件外发存储库接口
 * <p>
 * 定义将集成事件保存到外发存储的契约。
 *
 * @author zhangxihai
 */
public interface OutboxRepository {

    /**
     * 将集成事件保存到外发存储。
     *
     * @param event 要保存的集成事件。
     */
    void save(IntegrationEvent event);

    void save(DomainEvent event, String aggregateType, String aggregateId);

    void save(Iterable<? extends DomainEvent> events, String aggregateType, String aggregateId);

    List<Outbox> findUnset(int limit);

    boolean markSending(Long id);

    boolean markSent(Long id);

    boolean markFailed(Long id);
}
