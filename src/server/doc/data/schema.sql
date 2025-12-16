-- ----------------------------
-- Table structure for medias
-- ----------------------------
CREATE TABLE IF NOT EXISTS medias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    bucket_type VARCHAR(20) NOT NULL COMMENT '存储桶类型: private, public',
    media_type VARCHAR(20) NOT NULL COMMENT '媒体类型: image, video, etc',
    md5 VARCHAR(32) NOT NULL COMMENT '文件MD5',
    object_key VARCHAR(255) NOT NULL COMMENT '对象存储KEY',
    size_bytes BIGINT NOT NULL COMMENT '文件大小(字节)',
    content_type VARCHAR(100) COMMENT 'MIME类型',
    title VARCHAR(100) COMMENT '标题',
    description VARCHAR(500) COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_md5 (md5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体资源表';
