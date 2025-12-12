//package com.sgh.demo.general.quartz.quartz.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import lombok.Data;
//import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class QuartzConfig {
//
//    private final QuartzDataSourceProperties props;
//
//    // 通过构造器注入配置属性
//    public QuartzConfig(QuartzDataSourceProperties props) {
//        this.props = props;
//    }
//
//    @Bean
//    @QuartzDataSource
//    public DataSource quartzDataSource() {
//        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setDriverClassName(props.getDriver());
//        dataSource.setUrl(props.getUrl());
//        dataSource.setUsername(props.getUser());
//        dataSource.setPassword(props.getPassword());
////        dataSource.setMaximumPoolSize(props.getMaxConnections());
////        dataSource.setConnectionTestQuery(props.getValidationQuery());
//        return dataSource;
//    }
//
//
//
//}