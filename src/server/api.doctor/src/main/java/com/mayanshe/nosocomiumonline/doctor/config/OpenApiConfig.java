package com.mayanshe.nosocomiumonline.doctor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig: OpenAPI配置类
 *
 * @author zhangxihai
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Nosocomium Online Doctor系统接口文档")
                .description("Nosocomium Online Doctor系统接口文档")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("zhangxihai")
                        .email("mail@sniu.com")));
    }
}
