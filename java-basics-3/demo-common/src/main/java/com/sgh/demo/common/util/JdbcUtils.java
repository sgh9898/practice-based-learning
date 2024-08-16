package com.sgh.demo.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC 工具类
 *
 * @author Song gh
 * @version 2024/8/5
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
     * Mysql 查询, 返回指定的实体类, 为空时返回 null
     *
     * @param sql         查询sql
     * @param paramsMap   查询参数, sql 和 countSql 同时使用
     * @param resultClass 查询结果对应的实体类
     * @return 指定的实体类, 为空时返回 null
     */
    @Nullable
    public static <T> T queryForObject(NamedParameterJdbcTemplate npJdbcTemplate, String sql, @Nullable Map<String, Object> paramsMap, Class<T> resultClass) {
        paramsMap = checkBeforeQuery(sql, paramsMap);
        try {
            return npJdbcTemplate.queryForObject(sql, paramsMap, BeanPropertyRowMapper.newInstance(resultClass));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Mysql 查询, 返回指定的基础类数据(String, Long...), 为空时返回 null
     *
     * @param sql       查询sql
     * @param paramsMap 查询参数, sql 和 countSql 同时使用
     * @param baseClass 查询结果对应的基础类(String, Long...)
     * @return 指定的基础类数据(String, Long...), 为空时返回 null
     */
    @Nullable
    public static <T> T queryForObjectBaseType(NamedParameterJdbcTemplate npJdbcTemplate, String sql, @Nullable Map<String, Object> paramsMap, Class<T> baseClass) {
        paramsMap = checkBeforeQuery(sql, paramsMap);
        try {
            return npJdbcTemplate.queryForObject(sql, paramsMap, baseClass);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Mysql 列表查询, 指定返回的实体类
     *
     * @param sql         查询sql
     * @param paramsMap   查询参数, sql 和 countSql 同时使用
     * @param resultClass 查询结果对应的实体类
     */
    public static <T> List<T> queryForList(NamedParameterJdbcTemplate npJdbcTemplate, String sql, @Nullable Map<String, Object> paramsMap, Class<T> resultClass) {
        paramsMap = checkBeforeQuery(sql, paramsMap);
        return npJdbcTemplate.query(sql, paramsMap, BeanPropertyRowMapper.newInstance(resultClass));
    }

    /**
     * Mysql 列表查询, 返回指定的基础类数据(String, Long...)
     *
     * @param sql       查询sql
     * @param paramsMap 查询参数, sql 和 countSql 同时使用
     * @param baseClass 查询结果对应的基础类(String, Long...)
     */
    public static <T> List<T> queryForListBaseType(NamedParameterJdbcTemplate npJdbcTemplate, String sql, @Nullable Map<String, Object> paramsMap, Class<T> baseClass) {
        paramsMap = checkBeforeQuery(sql, paramsMap);
        return npJdbcTemplate.queryForList(sql, paramsMap, baseClass);
    }

    /**
     * Mysql 分页查询, 指定返回的实体类
     *
     * @param sql         查询sql
     * @param countSql    计数sql
     * @param paramsMap   查询参数, sql 和 countSql 同时使用
     * @param resultClass 查询结果对应的实体类
     * @param page        当前页码(0 为首页)
     * @param size        每页数据量
     */
    public static <T> Page<T> queryForPage(NamedParameterJdbcTemplate npJdbcTemplate, String sql, @Nullable String countSql,
                                           @Nullable Map<String, Object> paramsMap, Class<T> resultClass, int page, int size) {
        paramsMap = checkBeforeQuery(sql, paramsMap);
        // 未配置计数sql时, 进行默认配置
        if (StringUtils.isBlank(countSql)) {
            countSql = " select count(1) from (" + sql + ") jdbc_utils_temp_count ";
        }
        Long total = npJdbcTemplate.queryForObject(countSql, paramsMap, Long.class);
        List<T> list = new ArrayList<>();
        if (total != null && total > 0) {
            paramsMap.put("reservedStart", (page * size));
            paramsMap.put("reservedLength", size);
            String querySql = sql + " limit :reservedStart, :reservedLength ";
            list = npJdbcTemplate.query(querySql, paramsMap, BeanPropertyRowMapper.newInstance(resultClass));
        } else {
            total = 0L;
        }
        return new PageImpl<>(list, PageRequest.of(page, size), total);
    }

    /**
     * Mysql 分页查询, 返回指定的基础类数据(String, Long...)
     *
     * @param sql       查询sql
     * @param countSql  计数sql
     * @param paramsMap 查询参数, sql 和 countSql 同时使用
     * @param baseClass 查询结果对应的基础类(String, Long...)
     * @param page      当前页码(0 为首页)
     * @param size      每页数据量
     */
    public static <T> Page<T> queryForPageBaseType(NamedParameterJdbcTemplate npJdbcTemplate, String sql, @Nullable String countSql,
                                                   @Nullable Map<String, Object> paramsMap, Class<T> baseClass, int page, int size) {
        paramsMap = checkBeforeQuery(sql, paramsMap);
        // 未配置计数sql时, 进行默认配置
        if (StringUtils.isBlank(countSql)) {
            countSql = " select count(1) from (" + sql + ") jdbc_utils_temp_count ";
        }
        Long total = npJdbcTemplate.queryForObject(countSql, paramsMap, Long.class);
        List<T> list = new ArrayList<>();
        if (total != null && total > 0) {
            paramsMap.put("reservedStart", (page * size));
            paramsMap.put("reservedLength", size);
            String querySql = sql + " limit :reservedStart, :reservedLength ";
            list = npJdbcTemplate.queryForList(querySql, paramsMap, baseClass);
        } else {
            total = 0L;
        }
        return new PageImpl<>(list, PageRequest.of(page, size), total);
    }

    /**
     * Mysql 分页查询
     *
     * @param sql       查询sql
     * @param countSql  计数sql
     * @param paramsMap 查询参数, sql 和 countSql 同时使用
     * @param page      当前页码(0 为首页)
     * @param size      每页数据量
     */
    public static Page<Map<String, Object>> queryForPage(NamedParameterJdbcTemplate npJdbcTemplate, String sql, @Nullable String countSql,
                                                         @Nullable Map<String, Object> paramsMap, int page, int size) {
        if (paramsMap == null) {
            paramsMap = new HashMap<>();
        }
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        // 未配置计数sql时, 进行默认配置
        if (StringUtils.isBlank(countSql)) {
            countSql = " select count(1) from (" + sql + ") jdbc_utils_temp_count ";
        }
        Long total = npJdbcTemplate.queryForObject(countSql, paramsMap, Long.class);
        List<Map<String, Object>> list = new ArrayList<>();
        if (total != null && total > 0) {
            paramsMap.put("reservedStart", (page * size));
            paramsMap.put("reservedLength", size);
            String querySql = sql + " limit :reservedStart, :reservedLength ";
            list = npJdbcTemplate.queryForList(querySql, paramsMap);
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

    /** 查询前校验 */
    @NonNull
    private static Map<String, Object> checkBeforeQuery(String sql, Map<String, Object> paramsMap) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql 不能为空");
        }
        if (paramsMap == null) {
            paramsMap = new HashMap<>();
        }
        return paramsMap;
    }
}
