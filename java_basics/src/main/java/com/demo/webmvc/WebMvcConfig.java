package com.demo.webmvc;


import com.demo.aop.DemoFilter;
import com.demo.aop.DemoInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;

/**
 * @author Song gh on 2022/5/11.
 */
@EnableSwagger2
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // filter 作用的 path, 以 Controller 中 RequestMapping 开始
    private static final String FILTER_PATH = "/aop/filter/*";

    @Resource
    private DemoFilter demoFilter;

    @Resource
    private DemoInterceptor demoInterceptor;

    /** 解决跨域问题 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    /** 启用 filter */
    @Bean
    public FilterRegistrationBean<DemoFilter> registerFilter() {
        FilterRegistrationBean<DemoFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(demoFilter);
        registration.addUrlPatterns(FILTER_PATH);
        registration.setOrder(1);
        return registration;
    }

    /** 启用 interceptor */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(demoInterceptor).addPathPatterns("/aop/interceptor/**");
    }

    /** 避免 Swagger 无法访问 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui/index.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}


