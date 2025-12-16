package com.mayanshe.nosocomiumonline.infrastructure.cqrs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * CQRS 总线配置类。
 * 确保 core.application 中的 Handler 被扫描。
 */
@Configuration
@ComponentScan(basePackages = "com.mayanshe.nosocomiumonline.application.service.handler")
public class CqrsBusConfiguration {
}
