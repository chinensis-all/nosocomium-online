package com.mayanshe.nosocomiumonline.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * OutboxEntity: 集成事件外发实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEntity {
    private Long id;                        // 主键ID
    private String aggregateType;           // 聚合类型
    private String aggregateId;             // 聚合ID
    private String eventType;               // 事件类型
    private String payload;                 // 事件负载（JSON字符串）
    private String status;                  // NEW, SENT
    private Integer retryCount;             // 重试次数
    private LocalDateTime occurredAt;       // 事件发生时间
    private LocalDateTime createdAt;        // 记录创建时间
    private LocalDateTime sentAt;           // 事件发送时间
}
