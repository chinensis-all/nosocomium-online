package com.mayanshe.nosocomiumonline.infrastructure.config;

import com.mayanshe.nosocomiumonline.application.dynamic.DynamicMapperRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * MapperRegisterConfig: 映射注册配置类。
 */
@Configuration
@RequiredArgsConstructor
public class DynamicMapperRegisterConfig {
    private final DynamicMapperRegistry registry;

    @PostConstruct
    public void init() {

    }
}
