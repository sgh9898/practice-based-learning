package com.sgh.demo.sharding.swagger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * Swagger 配置(仅开发环境使用)
 *
 * @author Song gh on 2023/3/28.
 */
@Configuration
@EnableSwagger2
@Profile("dev")
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                // 自定义展示信息
                .apiInfo(apiInfo())
                // 自定义分页参数
                .alternateTypeRules(pageableTypeRule())
                // 配置扫描方式
                .select()
                // 扫描所有注解 @Api 的接口
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                // 扫描指定包
                // .apis(RequestHandlerSelectors.basePackage("com.demo.controller"))
                // 扫描全部
                // .apis(RequestHandlerSelectors.any())
                // 扫描全部路径
                .paths(PathSelectors.any())
                .build();
    }

    /** 避免 Swagger 无法访问 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解决静态资源无法访问
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        // 解决 swagger 无法访问
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui/index.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // 解决 swagger 的 js 文件无法访问
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /** 自定义展示信息 */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("接口文档(开发用)")
                .description("接口文档, 以及相关接口说明")
//                .contact(new Contact("author", "http://localhost:8080/demo/swagger-ui/", "email.com"))
                .version("1.0.0")
                .build();
    }

    /** 自定义分页参数 */
    private AlternateTypeRule pageableTypeRule() {
        return AlternateTypeRules.newRule(Pageable.class, Page.class, 1);
    }

    @Data
    @ApiModel
    public static class Page {
        @ApiModelProperty(value = "当前页码", example = "1")
        private Integer page;

        @ApiModelProperty(value = "每页数据量", example = "20")
        private Integer size;

        @ApiModelProperty(value = "排序字段, sort 可以传多次, 按先后顺序生效", example = "name 或 name, desc")
        private List<String> sort;
    }
}