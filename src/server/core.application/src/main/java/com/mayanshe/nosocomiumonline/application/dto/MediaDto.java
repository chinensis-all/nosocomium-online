package com.mayanshe.nosocomiumonline.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto {
    @Schema(description = "媒体ID", example = "1")
    private Long id;

    @Schema(description = "文件访问URL", example = "https://example.com/media/1")
    private String url;

    @Schema(description = "存储桶类型", example = "public")
    private String bucketType;

    @Schema(description = "媒体类型", example = "image")
    private String mediaType;

    @Schema(description = "MD5值", example = "qYgN3dlrsp3dCwCahqPPMucDNlzYjmcV")
    private String md5;

    @Schema(description = "文件大小（字节）", example = "2048")
    private Long sizeBytes;

    @Schema(description = "内容类型", example = "image/png")
    private String contentType;

    @Schema(description = "标题", example = "示例图片")
    private String title;

    @Schema(description = "描述", example = "这是一张示例图片")
    private String description;

    @Schema(description = "创建时间", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema
    private LocalDateTime updatedAt;
}
