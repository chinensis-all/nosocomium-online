package com.mayanshe.nosocomiumonline.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MediaInfoDto(
        @Schema(description = "标题", example = "我的文件")
    String title,
        @Schema(description = "描述", example = "这是我的文件的描述信息")
    String description
) {}
