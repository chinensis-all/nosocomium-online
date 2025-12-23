package com.mayanshe.nosocomiumonline.infrastructure.event;

import com.mayanshe.nosocomiumonline.domain.kernel.eventing.Outbox;

/**
 * MessageBus: 消息总线接口
 *
 * @author zhangxihai
 */
public interface MessageBus {
    void publish(Outbox event);
}
