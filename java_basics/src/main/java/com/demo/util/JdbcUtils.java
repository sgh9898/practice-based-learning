package com.demo.util;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JDBC 配置工具, 单数据源下可自动配置
 *
 * @author Song gh
 * @version 2024/2/5
 */
@Component
public class JdbcUtils {

    @Resource
    protected ApplicationContext applicationContext;

    @Getter
    private JdbcTemplate jdbcTemplate;
    @Getter
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /** 自动配置数据源 */
    @PostConstruct
    public void init() {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        jdbcTemplate = new JdbcTemplate(dataSource);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * 创建 JDBC template
     *
     * @param url      数据库url
     * @param username 用户名
     * @param password 密码
     */
    public static JdbcTemplate createJdbcTemplate(String url, String username, String password) {
        DriverManagerDataSource dataSource = getDataSource(DatabaseDriver.MYSQL.getDriverClassName(), url, username, password);
        return new JdbcTemplate(dataSource);
    }

    /**
     * 创建 JDBC template
     *
     * @param driverClassName 数据库driver
     * @param url             数据库url
     * @param username        用户名
     * @param password        密码
     */
    public static JdbcTemplate createJdbcTemplate(String driverClassName, String url, String username, String password) {
        DriverManagerDataSource dataSource = getDataSource(driverClassName, url, username, password);
        return new JdbcTemplate(dataSource);
    }

    /**
     * 创建 named JDBC template
     *
     * @param url      数据库url
     * @param username 用户名
     * @param password 密码
     */
    public static NamedParameterJdbcTemplate createNamedJdbcTemplate(String url, String username, String password) {
        DriverManagerDataSource dataSource = getDataSource(DatabaseDriver.MYSQL.getDriverClassName(), url, username, password);
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * 创建 named JDBC template
     *
     * @param driverClassName 数据库driver
     * @param url             数据库url
     * @param username        用户名
     * @param password        密码
     */
    public static NamedParameterJdbcTemplate createNamedJdbcTemplate(String driverClassName, String url, String username, String password) {
        DriverManagerDataSource dataSource = getDataSource(driverClassName, url, username, password);
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Mysql 分页查询, 指定返回的实体类
     *
     * @param sql         查询sql
     * @param countSql    计数sql
     * @param params      查询参数, sql 和 countSql 同时使用
     * @param resultClass 查询结果对应的实体类
     * @param page        当前页码(0 为首页)
     * @param size        每页数据量
     */
    @Transactional
    public <T> Page<T> queryForPage(NamedParameterJdbcTemplate npJdbcTemplate, String sql, @Nullable String countSql, Map<String, Object> params, Class<T> resultClass, int page, int size) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        // 未配置计数sql时, 进行默认配置
        if (StringUtils.isBlank(countSql)) {
            countSql = "select count(1) from (" + sql + ") jdbc_utils_temp_count";
        }
        Long total = npJdbcTemplate.queryForObject(countSql, params, Long.class);
        List<T> list = new ArrayList<>();
        if (total != null && total > 0) {
            params.put("reservedStart", (page * size));
            params.put("reservedLength", size);
            String querySql = sql + " limit :reservedStart,:reservedLength";
            list = npJdbcTemplate.query(querySql, params, BeanPropertyRowMapper.newInstance(resultClass));
        } else {
            total = 0L;
        }
        return new PageImpl<>(list, PageRequest.of(page, size), total);
    }

    /**
     * Mysql 分页查询
     *
     * @param sql      查询sql
     * @param countSql 计数sql
     * @param params   查询参数, sql 和 countSql 同时使用
     * @param page     当前页码(0 为首页)
     * @param size     每页数据量
     */
    @Transactional
    public Page<Map<String, Object>> queryForPage(NamedParameterJdbcTemplate npJdbcTemplate, String sql, @Nullable String countSql, Map<String, Object> params, int page, int size) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        if (size <= 0) {
            size = 10;
        }
        if (page < 0) {
            page = 0;
        }
        // 未配置计数sql时, 进行默认配置
        if (StringUtils.isBlank(countSql)) {
            countSql = "select count(1) from (" + sql + ") jdbc_utils_temp_count";
        }
        Long total = npJdbcTemplate.queryForObject(countSql, params, Long.class);
        List<Map<String, Object>> list = new ArrayList<>();
        if (total != null && total > 0) {
            params.put("reservedStart", (page * size));
            params.put("reservedLength", size);
            String querySql = sql + " limit :reservedStart,:reservedLength";
            list = npJdbcTemplate.queryForList(querySql, params);
        } else {
            total = 0L;
        }
        return new PageImpl<>(list, PageRequest.of(page, size), total);
    }

// ------------------------------ Private ------------------------------

    /**
     * 配置数据源
     *
     * @param driverClassName 数据库driver, 为空时默认使用 mysql
     * @param url             数据库url
     * @param username        用户名
     * @param password        密码
     */
    private static DriverManagerDataSource getDataSource(@Nullable String driverClassName, String url, String username, String password) {
        if (StringUtils.isBlank(driverClassName)) {
            driverClassName = DatabaseDriver.MYSQL.getDriverClassName();
        }
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
