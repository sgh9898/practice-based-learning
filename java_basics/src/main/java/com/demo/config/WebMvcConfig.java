package com.demo.config;


import com.demo.filter.DemoFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author Song gh on 2022/5/11.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // filter 作用的 path, 以 Controller 中 RequestMapping 开始
    private static final String FILTER_PATH = "/basic/filter/*";

    @Resource
    private DemoFilter demoFilter;

    @Bean
    public FilterRegistrationBean<DemoFilter> registerFilter() {
        FilterRegistrationBean<DemoFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(demoFilter);
        registration.addUrlPatterns(FILTER_PATH);
        registration.setOrder(1);
        return registration;
    }
}


