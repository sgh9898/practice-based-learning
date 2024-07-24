package com.sgh.demo.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JDBC 工具类
 *
 * @author Song gh
 * @version 2024/7/8
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcUtils {
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
     * @param driver   数据库driver
     * @param url      数据库url
     * @param username 用户名
     * @param password 密码
     */
    public static JdbcTemplate createJdbcTemplate(DatabaseDriver driver, String url, String username, String password) {
        DriverManagerDataSource dataSource = getDataSource(driver.getDriverClassName(), url, username, password);
        return new JdbcTemplate(dataSource);
    }

    /**
     * 创建 JDBC template
     *
     * @param driverClassName 数据库driver名称, {@link DatabaseDriver}
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
     * @param driver   数据库driver
     * @param url      数据库url
     * @param username 用户名
     * @param password 密码
     */
    public static NamedParameterJdbcTemplate createNamedJdbcTemplate(DatabaseDriver driver, String url, String username, String password) {
        DriverManagerDataSource dataSource = getDataSource(driver.getDriverClassName(), url, username, password);
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * 创建 named JDBC template
     *
     * @param driverClassName 数据库driver名称, {@link DatabaseDriver}
     * @param url             数据库url
     * @param username        用户名
     * @param password        密码
     */
    public static NamedParameterJdbcTemplate createNamedJdbcTemplate(String driverClassName, String url, String username, String password) {
        DriverManagerDataSource dataSource = getDataSource(driverClassName, url, username, password);
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Mysql 查询, 指定返回的实体类
     *
     * @param sql         查询sql
     * @param params      查询参数, sql 和 countSql 同时使用
     * @param resultClass 查询结果对应的实体类
     */
    public static <T> T queryForObject(NamedParameterJdbcTemplate npJdbcTemplate, String sql, Map<String, Object> params, Class<T> resultClass) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        return npJdbcTemplate.queryForObject(sql, params, BeanPropertyRowMapper.newInstance(resultClass));
    }

    /**
     * Mysql 列表查询, 指定返回的实体类
     *
     * @param sql         查询sql
     * @param params      查询参数, sql 和 countSql 同时使用
     * @param resultClass 查询结果对应的实体类
     */
    public static <T> List<T> queryForList(NamedParameterJdbcTemplate npJdbcTemplate, String sql, Map<String, Object> params, Class<T> resultClass) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        return npJdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(resultClass));
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
    public static <T> Page<T> queryForPage(NamedParameterJdbcTemplate npJdbcTemplate, String sql, @Nullable String countSql, Map<String, Object> params, Class<T> resultClass, int page, int size) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        // 未配置计数sql时, 进行默认配置
        if (StringUtils.isBlank(countSql)) {
            countSql = " select count(1) from (" + sql + ") jdbc_utils_temp_count ";
        }
        Long total = npJdbcTemplate.queryForObject(countSql, params, Long.class);
        List<T> list = new ArrayList<>();
        if (total != null && total > 0) {
            params.put("reservedStart", (page * size));
            params.put("reservedLength", size);
            String querySql = sql + " limit :reservedStart, :reservedLength ";
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
    public static Page<Map<String, Object>> queryForPage(NamedParameterJdbcTemplate npJdbcTemplate, String sql, @Nullable String countSql, Map<String, Object> params, int page, int size) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        // 未配置计数sql时, 进行默认配置
        if (StringUtils.isBlank(countSql)) {
            countSql = " select count(1) from (" + sql + ") jdbc_utils_temp_count ";
        }
        Long total = npJdbcTemplate.queryForObject(countSql, params, Long.class);
        List<Map<String, Object>> list = new ArrayList<>();
        if (total != null && total > 0) {
            params.put("reservedStart", (page * size));
            params.put("reservedLength", size);
            String querySql = sql + " limit :reservedStart, :reservedLength ";
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
     * @param productOrDriverClassName {@link DatabaseDriver} productName 或 driverClassName
     * @param url                      数据库url
     * @param username                 用户名
     * @param password                 密码
     */
    private static DriverManagerDataSource getDataSource(@Nullable String productOrDriverClassName, String url, String username, String password) {
        if (StringUtils.isBlank(productOrDriverClassName)) {
            productOrDriverClassName = DatabaseDriver.MYSQL.getDriverClassName();
        }
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
