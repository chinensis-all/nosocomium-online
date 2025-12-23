package com.mayanshe.nosocomiumonline.infrastructure.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "storage.cos")
public class CosConfig {
    private String secretId;
    private String secretKey;
    private String region;
    private String bucketPrivate;
    private String bucketPublic;
    private String cdnDomain;
    private Integer durationSeconds = 3600;
}
