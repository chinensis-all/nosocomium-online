package com.mayanshe.nosocomiumonline.domain.kernel.base;

import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import com.mayanshe.nosocomiumonline.shared.event.DomainEventCollector;
import com.mayanshe.nosocomiumonline.shared.valueobject.AggregateId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AggregateRoot: 聚合根基类
 * <p>
 * 支持捕获领域事件。
 * 
 * @author zhangxihai
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class AggregateRoot {

    private AggregateId id;

    protected void registerEvent(DomainEvent event) {
        DomainEventCollector.collect(event);
    }
}
