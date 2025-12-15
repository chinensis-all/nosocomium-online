package com.mayanshe.nosocomiumonline.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String status; // NEW, SENT
    private Integer retryCount;
    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}
