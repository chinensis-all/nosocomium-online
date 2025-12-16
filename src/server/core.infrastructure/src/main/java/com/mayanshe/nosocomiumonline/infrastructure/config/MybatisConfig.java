package com.mayanshe.nosocomiumonline.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis configuration to scan mappers in this module.
 */
@Configuration
@MapperScan("com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper")
public class MybatisConfig {
}
