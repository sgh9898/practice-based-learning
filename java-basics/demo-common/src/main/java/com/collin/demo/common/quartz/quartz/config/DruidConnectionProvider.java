package com.collin.demo.common.quartz.quartz.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.collin.demo.common.exception.BaseException;
import lombok.Getter;
import lombok.Setter;
import org.quartz.utils.ConnectionProvider;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Druid 数据库连接池配置
 *
 * @author Song gh
 * @version 2024/01/24
 */
@Getter
@Setter
@Configuration
public class DruidConnectionProvider implements ConnectionProvider {

    /** 数据库默认最大连接数 */
    private static final int DEFAULT_DB_MAX_CONNECTIONS = 10;

    /** 默认每个链接缓存120个预编译语句 */
    private static final int DEFAULT_DB_MAX_CACHED_STATEMENTS_PER_CONNECTION = 120;

// ------------------------------ 自动读取配置文件 ------------------------------
    /** 数据库驱动 */
    private String driver;
    /** 数据库 url */
    private String url;
    /** 数据库用户名 */
    private String user;
    /** 数据库密码 */
    private String password;
    /** 数据库最大连接数 */
    private int maxConnection;
    /** 测试数据库连接的 sql, 数据库SQL查询每次连接返回执行到连接池，以确保它仍然是有效的。 */
    private String validationQuery;
    /** 每个链接最多缓存预编译语句的数量 */
    private String maxCachedStatementsPerConnection;
    /** 是否每次从池中取连接时, 验证连接可用性 */
    private boolean validateOnCheckout;
    /** 空闲连接超过当前值时进行验证, 单位: 秒 */
    private int idleConnectionValidationSeconds;
    /** 空闲连接超过当前值时丢弃, 单位: 秒 */
    private int discardIdleConnectionsSeconds;
// ============================== 自动读取配置文件 End ==============================

    /** Druid 连接池 */
    private DruidDataSource datasource;

    @Override
    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    @Override
    public void shutdown() {
        datasource.close();
    }

    @Override
    public void initialize() throws SQLException {
        if (this.url == null) {
            throw new SQLException("连接池创建失败: url 不能为空");
        }
        if (this.driver == null) {
            throw new SQLException("连接池创建失败: driver 不能为空");
        }
        if (this.maxConnection < 0) {
            throw new SQLException("连接池创建失败: 最大连接数不能小于 0");
        }

        datasource = new DruidDataSource();
        try {
            datasource.setDriverClassName(this.driver);
        } catch (Exception e) {
            throw new BaseException("配置 driver 失败: " + e.getMessage(), e);
        }

        datasource.setUrl(this.url);
        datasource.setUsername(this.user);
        datasource.setPassword(this.password);
        datasource.setMaxActive(this.maxConnection);
        datasource.setMinIdle(1);
        datasource.setMaxWait(0);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(DEFAULT_DB_MAX_CONNECTIONS);

        if (this.validationQuery != null) {
            datasource.setValidationQuery(this.validationQuery);
            if (!this.validateOnCheckout) {
                datasource.setTestOnReturn(true);
            } else {
                datasource.setTestOnBorrow(true);
                datasource.setValidationQueryTimeout(this.idleConnectionValidationSeconds);
            }
        }
    }
}