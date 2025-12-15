CREATE DATABASE IF NOT EXISTS `nosocomium-online` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `nosocomium-online`;
SET NAMES utf8mb4;

-- 可靠事件传递 - Outbox Table
CREATE TABLE IF NOT EXISTS `event_outbox`
(
    `id`             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `aggregate_type` VARCHAR(64)  NOT NULL COMMENT '聚合类型',
    `aggregate_id`   VARCHAR(64)  NOT NULL COMMENT '聚合ID',
    `event_type`     VARCHAR(128) NOT NULL COMMENT '事件类型',
    `payload`        JSON         NOT NULL COMMENT '事件负载',
    `status`         VARCHAR(32)  NOT NULL DEFAULT 'NEW' COMMENT '事件状态 (NEW, PROCESSING, SENT, FAILED)',
    `retry_count`    INT          NOT NULL DEFAULT 0 COMMENT '重试次数',
    `occurred_at`    DATETIME(6)  NOT NULL COMMENT '事件发生时间',
    `created_at`     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `sent_at`        DATETIME(6)           DEFAULT NULL COMMENT '发送时间',
    INDEX `idx_outbox_status_created` (`status`, `created_at`) USING BTREE COMMENT '按状态和创建时间索引',
    INDEX `idx_outbox_aggregate` (`aggregate_type`, `aggregate_id`) USING BTREE COMMENT '按聚合类型和ID索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;