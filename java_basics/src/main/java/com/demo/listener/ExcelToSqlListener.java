package com.demo.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.demo.exception.BaseException;
import com.demo.pojo.ExcelToSql;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [监听] Excel 转 Sql 建表语句
 * <br> 不能被 spring 管理, 每次读取 excel 都要 new ,用到 spring 可以构造方法传进来
 *
 * @author Song gh on 2022/3/25.
 */
@Getter
public class ExcelToSqlListener extends AnalysisEventListener<ExcelToSql> {
    private static final Logger log = LoggerFactory.getLogger(ExcelToSqlListener.class);

    // Basics
    private final ExcelToSql excelToSql;
    private int cnt = 0;
    private final StringBuffer ddl = new StringBuffer();
    private String ddlStr;
    private final String alias;

    // 自定义
    /** invoke 循环间隔, 每 x 条数据 */
    private static final int BATCH_COUNT = 500;
    /** 默认 varchar 长度 */
    private static final String DEFAULT_VARCHAR = "varchar(128)";

    /** constructor */
    public ExcelToSqlListener(String tableName, String alias) {
        // 校验空字段
        if (StringUtils.isBlank(tableName)) {
            tableName = "untitled_table";
        }

        // 数据库表别名
        this.alias = alias;

        this.excelToSql = new ExcelToSql();
        this.ddl.append("create table if not exists ")
                .append(tableName.toLowerCase().trim())
                .append(" (");
    }

    /**
     * 循环数据处理
     *
     * @param excelToSql 单行数据
     * @param context    context
     */
    @Override
    public void invoke(ExcelToSql excelToSql, AnalysisContext context) {
        // 单行数据
        dealWithOneRow(excelToSql);
        cnt++;

        // 达到单次处理上限
        if (cnt >= BATCH_COUNT) {
            // Todo: do something here
            cnt = 0;
        }
    }

    /** 处理最后批次数据 */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 删除最后一个逗号
        ddl.deleteCharAt(ddl.lastIndexOf(","));

        // 数据库表有无别名
        ddl.append(StringUtils.isNotBlank(alias) ? ") comment '" + alias + "';" : ");");

        this.ddlStr = ddl.toString();
        log.debug("构建 ddl 完成: {}", ddlStr);
    }

    /** 根据 excel 单行数据构建 ddl */
    private void dealWithOneRow(ExcelToSql excelToSql) {
        // 校验必填项 name, type
        if (StringUtils.isBlank(excelToSql.getName()) || StringUtils.isBlank(excelToSql.getType())) {
            throw new BaseException("参数名或类型为空");
        }

        // name
        String name = excelToSql.getName().trim();
        ddl.append(name).append(' ');

        // type
        String type = excelToSql.getType()
                .toLowerCase().trim()
                .replace('（', '(')
                .replace('）', ')')      // brackets in Chinese
                .replace("long", "bigint")
                .replace("string", DEFAULT_VARCHAR);    // Java type to Database type
        ddl.append(type.equals("varchar") ? DEFAULT_VARCHAR : type)
                .append(' ');       // 未定长度按默认值

        // required
        String required = excelToSql.getRequired().trim();
        ddl.append(required.equals("是") || required.equals("必填") ? "not null" : "null")
                .append(' ');

        // comment
        String comment = excelToSql.getComment().trim();
        ddl.append("comment '")
                .append(comment)
                .append("'");

        // primary key
        String primaryKey = excelToSql.getPrimaryKey().trim();
        ddl.append(primaryKey.equals("是") ? " primary key," : ", ");
    }

}