package com.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger 配置
 * <br> 生产环境需要关闭 (springfox.documentation.swagger-ui=false)
 *
 * @author Song gh on 2022/3/28.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                // 自定义展示信息
                .apiInfo(apiInfo())
                // 全局通用变量
                // .globalRequestParameters(parameters())
                // 配置扫描方式
                .select()
                // 扫描指定 Class
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                // 扫描所有注解的 api
                // .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 扫描指定包
                // .apis(RequestHandlerSelectors.basePackage("com.demo.controller"))
                // 扫描全部
                // .apis(RequestHandlerSelectors.any())
                // 扫描全部路径
                .paths(PathSelectors.any())
                .build();
    }

    /** 自定义展示信息 */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Demo 项目")
                .description("接口文档(开发用)")
                .contact(new Contact("author", "http://localhost:8080/demo/swagger-ui/", "email.com"))
                .version("1.0.0")
                .build();
    }

    /** 全局通用变量 */
    private List<RequestParameter> parameters() {
        List<RequestParameter> params = new ArrayList<>();
        params.add(new RequestParameterBuilder()
                .name("token")
                .description("认证 token")
                // 获取形式
                .in(ParameterType.QUERY)
                // 数据格式
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(true)
                .build());
        return params;
    }
}