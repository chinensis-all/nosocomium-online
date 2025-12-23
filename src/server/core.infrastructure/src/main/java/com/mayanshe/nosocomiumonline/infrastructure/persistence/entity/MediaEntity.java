package com.mayanshe.nosocomiumonline.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaEntity {
    private Long id;

    private String bucketType;

    private String mediaType;

    private String md5;

    private String objectKey;

    private Long sizeBytes;

    private String contentType;

    private String title;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long deletedAt;
}
