package com.demo.config;


import com.demo.aop.DemoFilter;
import com.demo.aop.DemoInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author Song gh on 2022/5/11.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // filter 作用的 path, 以 Controller 中 RequestMapping 开始
    private static final String FILTER_PATH = "/aop/filter/*";

    @Resource
    private DemoFilter demoFilter;

    @Resource
    private DemoInterceptor demoInterceptor;

    @Bean
    public FilterRegistrationBean<DemoFilter> registerFilter() {
        FilterRegistrationBean<DemoFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(demoFilter);
        registration.addUrlPatterns(FILTER_PATH);
        registration.setOrder(1);
        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(demoInterceptor).addPathPatterns("/aop/interceptor/**");
    }
}


