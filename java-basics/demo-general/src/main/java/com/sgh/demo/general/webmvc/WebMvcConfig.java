package com.sgh.demo.general.webmvc;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sgh.demo.general.aop.DemoFilter;
import com.sgh.demo.general.aop.DemoInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

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

    /** 解决跨域问题 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    /** 前后端数据交互 */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        // 传参时将 long 转为 string, 解决精度丢失问题
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        // 修复时间转化报错
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());

        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(jackson2HttpMessageConverter);
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
}


