package com.mayanshe.nosocomiumonline.shared.event;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * DomainEvent: 领域事件接口
 * <p>
 * 领域事件表示在领域模型中发生的重要事件，通常用于触发其他操作或通知系统的其他部分。
 *
 * @author zhangxihai
 */
public interface  DomainEvent extends Serializable {
    LocalDateTime occurredAt();
}
