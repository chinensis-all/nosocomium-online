CREATE DATABASE IF NOT EXISTS `nosocomium-online` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `nosocomium-online`;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `event_outbox` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `aggregate_type` VARCHAR(64) NOT NULL,
  `aggregate_id` VARCHAR(64) NOT NULL,
  `event_type` VARCHAR(128) NOT NULL,
  `payload` JSON NOT NULL,
  `status` VARCHAR(32) NOT NULL DEFAULT 'NEW',
  `retry_count` INT NOT NULL DEFAULT 0,
  `occurred_at` DATETIME(6) NOT NULL,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `sent_at` DATETIME(6),
  INDEX `idx_outbox_status_created` (`status`, `created_at`),
  INDEX `idx_outbox_aggregate` (`aggregate_type`, `aggregate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;