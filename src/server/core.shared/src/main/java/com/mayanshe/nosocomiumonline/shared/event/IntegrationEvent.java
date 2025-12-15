package com.mayanshe.nosocomiumonline.shared.event;

import java.time.LocalDateTime;

/**
 * IntegrationEvent: 集成事件接口
 * <p>
 * 用于跨模块或跨服务的通信。
 * 必须可序列化为JSON。
 */
public interface IntegrationEvent {
    /**
     * 事件类型 (e.g. "PatientRegistered").
     */
    String eventType();

    /**
     * Type of the Aggregate Root (e.g. "Patient").
     */
    /**
     * 聚合根类型 (e.g. "Patient").
     */
    String aggregateType();

    /**
     * 事件关联的聚合根ID (e.g. "patient-12345").
     */
    String aggregateId();

    /**
     * 事件发生时间
     */
    LocalDateTime occurredAt();
}
