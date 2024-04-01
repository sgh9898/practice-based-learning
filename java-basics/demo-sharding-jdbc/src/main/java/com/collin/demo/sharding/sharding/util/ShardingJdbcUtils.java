package com.collin.demo.sharding.sharding.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Sharding 分库分表使用的 JDBC 工具
 *
 * @author Song gh
 * @version 2024/3/13
 */
@Slf4j
@Component
public class ShardingJdbcUtils {

    // 数据库配置, 需要在配置文件中进行配置
    private static String dsUrl;
    private static String dsUsername;
    private static String dsPassword;

    /** [构造] */
    protected ShardingJdbcUtils(@Value("${sharding.createTable.byDate.dsUrl:#{null}}") String tempDsUrl,
                                @Value("${sharding.createTable.byDate.dsUsername:#{null}}") String tempDsUsername,
                                @Value("${sharding.createTable.byDate.dsPassword:#{null}}") String tempDsPassword
    ) {
        dsUrl = tempDsUrl;
        dsUsername = tempDsUsername;
        dsPassword = tempDsPassword;
    }

    /**
     * 创建表
     *
     * @param actualTableName 新建的表名
     * @param logicTableName  逻辑表名(同时作为建表模板)
     */
    public static void createTable(String actualTableName, String logicTableName) {
        synchronized (actualTableName.intern()) {
            try {
                JdbcTemplate jdbcTemplate = createJdbcTemplate(dsUrl, dsUsername, dsPassword);
                String sql = "CREATE TABLE IF NOT EXISTS `" + actualTableName + "` LIKE `" + logicTableName + "`;";
                jdbcTemplate.execute(sql);
            } catch (DataAccessException e) {
                log.error("Sharding 自动创建表失败, 表名: {}, 报错原因: {}", actualTableName, e.getMessage(), e);
                throw new IllegalArgumentException("Sharding 自动创建表失败, 表名: " + actualTableName);
            }
        }
    }

    /**
     * 根据逻辑表名获取全部实际表
     *
     * @param logicTableName    逻辑表名
     * @param shardingSeparator 逻辑表名与分表后缀的分隔符
     */
    public static List<String> getActualTables(String logicTableName, String shardingSeparator) {
        List<String> actualTableNameList = new LinkedList<>();
        JdbcTemplate jdbcTemplate = createJdbcTemplate(dsUrl, dsUsername, dsPassword);
        String sql = "SHOW TABLES LIKE '" + logicTableName + shardingSeparator + "%'; ";
        List<String> tableNameList = jdbcTemplate.queryForList(sql, String.class);
        String tableNameRegex = logicTableName + shardingSeparator + "\\d+";
        // 其他表名可能包含当前逻辑表名, 排除无关的表
        for (String tableName : tableNameList) {
            if (tableName.matches(tableNameRegex)) {
                actualTableNameList.add(tableName);
            }
        }
        return actualTableNameList;
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
