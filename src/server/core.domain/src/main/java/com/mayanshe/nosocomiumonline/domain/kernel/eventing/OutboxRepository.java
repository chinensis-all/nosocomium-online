package com.mayanshe.nosocomiumonline.domain.kernel.eventing;

import com.mayanshe.nosocomiumonline.shared.event.IntegrationEvent;


/**
 * OutboxRepository: 集成事件外发存储库接口
 * <p>
 * 定义将集成事件保存到外发存储的契约。
 */
public interface OutboxRepository {

    /**
     * 将集成事件保存到外发存储。
     *
     * @param event 要保存的集成事件。
     */
    void save(IntegrationEvent event);
}
