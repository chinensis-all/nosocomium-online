-- Cleaned up for H2
SET MODE MySQL;

-- 可靠事件传递
CREATE TABLE IF NOT EXISTS `_events`
(
    `id`             BIGINT PRIMARY KEY COMMENT 'ID',
    `aggregate_type` VARCHAR(64)                               NOT NULL COMMENT '聚合类型',
    `aggregate_id`   VARCHAR(64)                               NOT NULL COMMENT '聚合ID',
    `event_type`     VARCHAR(128)                              NOT NULL COMMENT '事件类型',
    `payload`        TEXT                                      NOT NULL COMMENT '事件负载',
    `status`         VARCHAR(20)                               NOT NULL DEFAULT 'new' COMMENT '事件状态 (new, sending, sent, failed)',
    `retry_count`    INT                                       NOT NULL DEFAULT 0 COMMENT '重试次数',
    `occurred_at`    DATETIME(6)                               NOT NULL COMMENT '事件发生时间',
    `sent_at`        DATETIME(6)                               NOT NULL COMMENT '发送时间',
    `created_at`     DATETIME(6)                               NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `updated_at`     DATETIME                                  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_outbox_status_created` (`status`, `created_at`) USING BTREE COMMENT '按状态和创建时间索引',
    INDEX `idx_outbox_aggregate` (`aggregate_type`, `aggregate_id`) USING BTREE COMMENT '按聚合类型和ID索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `regions`
(
    `id`           bigint unsigned                         NOT NULL COMMENT '行政编码',
    `parent_id`    bigint unsigned                         NOT NULL DEFAULT '0' COMMENT '上级行政编码',
    `region_level` tinyint unsigned                        NOT NULL DEFAULT '1' COMMENT '行政区划级别 1:省 2:市 3:区/县 4:镇/街道 5:村/社区',
    `postal_code`  char(6) COLLATE utf8mb4_unicode_ci      NOT NULL DEFAULT '' COMMENT '邮政编码',
    `area_code`    char(6) COLLATE utf8mb4_unicode_ci      NOT NULL DEFAULT '' COMMENT '区号',
    `region_name`  varchar(50) COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT '' COMMENT '行政区划名称',
    `name_pinyin`  varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '行政区划名称拼音',
    `short_name`   varchar(50) COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT '' COMMENT '行政区划简称',
    `merge_name`   varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '行政区划组合名称',
    `longitude`    decimal(10, 6)                          NOT NULL DEFAULT '0.000000' COMMENT '经度',
    `latitude`     decimal(10, 6)                          NOT NULL DEFAULT '0.000000' COMMENT '纬度',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`) USING BTREE COMMENT '上级行政编码索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='中国行政区划表';

-- 媒体资源表
CREATE TABLE IF NOT EXISTS `medias`
(
    `id`           BIGINT PRIMARY KEY COMMENT '主键ID',
    `bucket_type`  ENUM ('private', 'public')                                       NOT NULL DEFAULT 'private' COMMENT '存储桶类型: private=私读, public=共读',
    `media_type`   ENUM ('image', 'audio', 'video', 'document', 'archive', 'other') NOT NULL DEFAULT 'other' COMMENT '媒体类型: image=图片, audio=音频, video=视频, document=文档, archive=压缩包, other=其他',
    `md5`          VARCHAR(32)                                                      NOT NULL COMMENT '文件MD5',
    `object_key`   VARCHAR(255)                                                     NOT NULL COMMENT '对象存储KEY',
    `size_bytes`   BIGINT                                                           NOT NULL DEFAULT 0 COMMENT '文件大小(字节)',
    `content_type` VARCHAR(100)                                                     NOT NULL DEFAULT '' COMMENT 'MIME类型',
    `title`        VARCHAR(100)                                                     NOT NULL DEFAULT '' COMMENT '标题',
    `description`  VARCHAR(500)                                                     NOT NULL DEFAULT '' COMMENT '描述',
    `created_at`   DATETIME                                                         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME                                                         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_md5` (`md5`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='媒体资源表';
