package com.mayanshe.nosocomiumonline.shared.event;

import java.util.ArrayList;
import java.util.List;

/***
 * DomainEventCollector: 领域事件收集器
 *
 * 用于在应用程序中收集和管理领域事件，通常在处理业务逻辑时使用。
 *
 * @author zhangxihai
 */
public final class DomainEventCollector {
    /**
     * 线程本地存储的领域事件列表，确保每个线程都有独立的事件收集器。
     */
    private static final ThreadLocal<List<DomainEvent>> EVENTS =
            ThreadLocal.withInitial(ArrayList::new);

    public static void collect(DomainEvent event) {
        EVENTS.get().add(event);
    }

    /***
     * 清空并返回当前线程收集的所有领域事件。
     *
     * @return 当前线程收集的领域事件列表的不可变副本。
     */
    public static List<DomainEvent> drain() {
        try {
            return List.copyOf(EVENTS.get());
        } finally {
            EVENTS.remove();
        }
    }

    /**
     * 清除当前线程的领域事件收集器。
     */
    public static void clear() {
        EVENTS.remove();
    }
}
