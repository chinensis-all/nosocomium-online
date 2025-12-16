package com.mayanshe.nosocomiumonline.application.dto;

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
    private Long id;
    private String url;
    private String bucketType;
    private String mediaType;
    private String md5;
    private Long sizeBytes;
    private String contentType;
    private String title;
    private String description;
    private LocalDateTime createdAt;
}
