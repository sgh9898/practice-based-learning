package com.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 配置 (Spring Doc)
 *
 * @author Song gh on 2022/3/28.
 */
@Configuration
public class SwaggerConfig {

    /** 自定义展示信息 */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Demo API")
                        .description("Java basics that can be used as a template.")
                        .version("v0.0.1"));
    }
}