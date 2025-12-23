package com.mayanshe.nosocomiumonline.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * OutboxEntity: 集成事件外发实体
 *
 * @author zhangxihai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEntity {
    private Long id;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private String payload;
    private String status;
    private Integer retryCount;
    private LocalDateTime sentAt;
    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
