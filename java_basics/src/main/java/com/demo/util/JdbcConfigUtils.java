package com.demo.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JDBC 配置工具
 *
 * @author Song gh on 2023/5/12.
 */
public class JdbcConfigUtils {

    /**
     * 自行配置数据源, 创建 JDBC template
     *
     * @param productOrDriverClassName {@link DatabaseDriver} productName 或 driverClassName
     * @param url                      数据库url
     * @param username                 用户名
     * @param password                 密码
     */
    public static JdbcTemplate createJdbcTemplate(String productOrDriverClassName, String url, String username, String password) {
        DriverManagerDataSource dataSource = getDataSource(productOrDriverClassName, url, username, password);
        return new JdbcTemplate(dataSource);
    }

    /**
     * 自行配置数据源, 创建 named JDBC template
     *
     * @param productOrDriverClassName {@link DatabaseDriver} productName 或 driverClassName
     * @param url                      数据库url
     * @param username                 用户名
     * @param password                 密码
     */
    public static NamedParameterJdbcTemplate createNamedJdbcTemplate(String productOrDriverClassName, String url, String username, String password) {
        DriverManagerDataSource dataSource = getDataSource(productOrDriverClassName, url, username, password);
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Mysql 分页查询
     *
     * @param sql         sql
     * @param params      查询条件
     * @param resultClass 查询结果对应的类
     * @param pageable    分页参数
     */
    @Transactional
    public <T> Page<T> queryForPageMysql(String sql, Map<String, Object> params, Class<T> resultClass, NamedParameterJdbcTemplate npJdbcTemplate, Pageable pageable) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        if (pageable == null || pageable.getPageNumber() <= 0) {
            pageable = PageRequest.of(1, 20);
        }
        String countSql = "select count(1) from (" + sql + ") t";
        Long total = npJdbcTemplate.queryForObject(countSql, params, Long.class);
        List<T> list = new ArrayList<>();
        if (total != null && total > 0) {
            params.put("reservedStart", (pageable.getPageNumber() - 1) * pageable.getPageSize());
            params.put("reservedLength", pageable.getPageSize());
            String querySql = sql + " limit :reservedStart,:reservedLength";
            list = npJdbcTemplate.query(querySql, params, BeanPropertyRowMapper.newInstance(resultClass));
        } else {
            total = 0L;
        }
        return new PageImpl<>(list, pageable, total);
    }

    /**
     * Mysql 分页查询
     *
     * @param sql      sql
     * @param params   查询条件
     * @param pageable 分页参数
     */
    @Transactional
    public Page<Map<String, Object>> queryForPageMysql(String sql, Map<String, Object> params, NamedParameterJdbcTemplate npJdbcTemplate, Pageable pageable) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        if (pageable == null || pageable.getPageNumber() <= 0) {
            pageable = PageRequest.of(1, 20);
        }
        String countSql = "select count(1) from (" + sql + ") t";
        Long total = npJdbcTemplate.queryForObject(countSql, params, Long.class);
        List<Map<String, Object>> list = new ArrayList<>();
        if (total != null && total > 0) {
            params.put("reservedStart", (pageable.getPageNumber() - 1) * pageable.getPageSize());
            params.put("reservedLength", pageable.getPageSize());
            String querySql = sql + " limit :reservedStart,:reservedLength";
            list = npJdbcTemplate.queryForList(querySql, params);
        } else {
            total = 0L;
        }
        return new PageImpl<>(list, pageable, total);
    }

// ------------------------------ Private ------------------------------

    /**
     * 配置数据源
     *
     * @param productOrDriverClassName {@link DatabaseDriver} productName 或 driverClassName
     * @param url                      数据库url
     * @param username                 用户名
     * @param password                 密码
     */
    private static DriverManagerDataSource getDataSource(String productOrDriverClassName, String url, String username, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        // 配置 driverClass
        if (DatabaseDriver.fromProductName(productOrDriverClassName) != DatabaseDriver.UNKNOWN) {
            dataSource.setDriverClassName(DatabaseDriver.fromProductName(productOrDriverClassName).getDriverClassName());
        } else {
            dataSource.setDriverClassName(productOrDriverClassName);
        }
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
