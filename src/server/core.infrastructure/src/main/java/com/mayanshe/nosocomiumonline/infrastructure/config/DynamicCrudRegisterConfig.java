package com.mayanshe.nosocomiumonline.infrastructure.config;

import com.mayanshe.nosocomiumonline.application.dynamic.DynamicCrudConfigRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DynamicCrudRegisterConfig {
    private final DynamicCrudConfigRegistry registry;

    @PostConstruct
    public void init() {
        // 在此处注册动态 CRUD 配置
    }
}
