package com.mayanshe.nosocomiumonline.domain.kernel.base;

import com.mayanshe.nosocomiumonline.domain.kernel.eventing.DomainEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AggregateRoot: 聚合根基类
 * <p>
 * 支持捕获领域事件。
 */
public abstract class AggregateRoot {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 注册一个新的领域事件
     *
     * @param event 要注册的领域事件
     */
    protected void registerEvent(DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    /**
     * 清除注册的领域事件
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    /**
     * 获取已注册的领域事件列表
     *
     * @return 不可修改的领域事件列表
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }
}
