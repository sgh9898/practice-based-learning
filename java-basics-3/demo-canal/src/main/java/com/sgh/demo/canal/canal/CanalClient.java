package com.sgh.demo.canal.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Canal Client 功能
 * <pre>
 * 1. 启用监听功能
 *   1) [推荐] 使用定时任务启动, 意外中断时会自动重连: {@link #scheduledStart}
 *   2) 单次启动, 中断时不会自动重连: {@link #startOnce}
 * 2. 数据分流, 可以按表或者事件类型(insert/update/delete)对监听的数据进行分流: {@link #processData }
 * 3. 分流后的数据处理
 *   1) 插入时 {@link #afterEventInsert}
 *   2) 更新时 {@link #afterEventUpdate}
 *   3) 删除时 {@link #afterEventDelete} </pre>
 *
 * @author Song gh
 * @version 2024/11/25
 */
@Slf4j
@Component
public class CanalClient {

// ------------------------------ 运行参数 ------------------------------
    /** 单批次处理的数据量 */
    private static final Integer BATCH_SIZE = 1000;

    /** 最多 x 批次数据为空(仅在 ≥ 0 时生效), 到达计数时 Canal 停止运行 */
    private static final Integer MAX_EMPTY_BATCH_COUNT = -1;

    /** 当前批次数据为空后, 延迟 x 秒再执行 */
    private static final Integer EMPTY_BATCH_DELAY = 5;

// ---------- Canal Deployer 参数(需要匹配 Canal Deployer 部署参数, 不可仅在项目内修改) ----------
    /** canal 使用的数据库账号: 用户名 */
    @Value("${canal.username:canal}")
    private String canalUsername;

    /** canal 使用的数据库账号: 密码 */
    @Value("${canal.password:canal}")
    private String canalPassword;

    /** 部署 canal 的 host, 默认为本机 localhost */
    @Value("${canal.host:localhost}")
    private String canalHost;

    /** canal 所使用的端口(canal.properties 中 canal.port), 默认 11111 */
    @Value("${canal.port:11111}")
    private Integer canalPort;

    /** canal instance 名称, 默认为 example */
    @Value("${canal.port:example}")
    private String canalInstance;

    /**
     * canal 监听的表, 未配置时以 canal deployer 为准
     * 监听全表             .*\..*
     * 监听指定 schema      test\..*
     * 监听指定表            test.table1
     * 综合监听(逗号分隔)     test1\..*,test2.table1,test2.table2
     */
    @Value("${canal.subscribe:#{'.*\\..*'}}")
    private String subscribeRegex;

// ------------------------------ 业务参数 ------------------------------
    /** 备份表所属 schema */
    @NonNull
    @Value("${canal.extension.slave.schema:#{''}}")
    private String backupSchema;

    /** 备份表统一前缀, 格式统一为下划线 _ 结尾 */
    @NonNull
    @Value("${canal.extension.slave.table-name-prefix:#{''}}")
    private String backupPrefix;

    @Resource
    private JdbcTemplate jdbcTemplate;

// ------------------------------ 启动 ------------------------------

    /** 以定时任务的方式启动 Canal, 在意外中断时自动重连 */
    @Scheduled(fixedDelayString = "PT1S", initialDelayString = "PT10S")
    private void scheduledStart() {
        startOnce();
    }

// ------------------------------ 主要方法 ------------------------------

    /**
     * 监听到数据删除时进行的处理
     *
     * @param header  数据库, 事件类型等信息: {@link Header#getSchemaName() 库名}, {@link Header#getTableName() 表名}
     * @param rowData 具体数据: {@link RowData#getBeforeColumnsList() 原数据}, {@link RowData#getAfterColumnsList() 新数据}
     */
    private void afterEventDelete(Header header, RowData rowData) {
        // 根据需求配置
    }

    /**
     * 监听到数据插入时进行的处理
     *
     * @param header  数据库, 事件类型等信息: {@link Header#getSchemaName() 库名}, {@link Header#getTableName() 表名}
     * @param rowData 具体数据: {@link RowData#getBeforeColumnsList() 原数据}, {@link RowData#getAfterColumnsList() 新数据}
     */
    private void afterEventInsert(Header header, RowData rowData) {
        // 生成 sql 语句
        StringBuilder insertFields = new StringBuilder("insert into " + backupSchema + backupPrefix + header.getTableName() + " (");
        StringBuilder insertValues = new StringBuilder("values (");
        buildUpSqlThenExecute(header, rowData, insertFields, insertValues);
    }

    /**
     * 监听到数据更新时进行的处理
     *
     * @param header  数据库, 事件类型等信息: {@link Header#getSchemaName() 库名}, {@link Header#getTableName() 表名}
     * @param rowData 具体数据: {@link RowData#getBeforeColumnsList() 原数据}, {@link RowData#getAfterColumnsList() 新数据}
     */
    private void afterEventUpdate(Header header, RowData rowData) {
        // 生成 sql 语句
        StringBuilder insertFields = new StringBuilder("replace into " + backupSchema + backupPrefix + header.getTableName() + " (");
        StringBuilder insertValues = new StringBuilder("values (");
        buildUpSqlThenExecute(header, rowData, insertFields, insertValues);
    }

// ------------------------------ 其他 ------------------------------

    /**
     * Canal 启动(单次), 中断后不会重连
     * <br> 推荐通过定时任务的方式启动, 以确保在中断时自动重连
     */
    private void startOnce() {
        // 链接数据库
        log.info("Canal 开始运行");
        CanalConnector connector = createConnector();

        // 处理从表信息
        if (StringUtils.isNotBlank(backupSchema) && !backupSchema.endsWith(".")) {
            backupSchema += ".";
        }
        if (StringUtils.isNotBlank(backupPrefix) && !backupPrefix.endsWith("_")) {
            backupPrefix += "_";
        }
        // 进行数据监控
        dataMonitoring(connector);
    }

    /** 创建 Canal 连接 */
    @NonNull
    private CanalConnector createConnector() {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalHost,
                canalPort), canalInstance, canalUsername, canalPassword);
        connector.connect();
        // 订阅的数据库表, 未配置时以 canal deployer 为准
        if (StringUtils.isNotBlank(subscribeRegex)) {
            subscribeRegex = subscribeRegex.replaceAll("\\s*", "");
            connector.subscribe(subscribeRegex);
        } else {
            connector.subscribe();
        }
        // 回滚至未进行 ack 的地方
        connector.rollback();
        return connector;
    }

    /** Canal 监听数据 */
    private void dataMonitoring(CanalConnector connector) {
        try {
            // 持续监听数据库, 连续为空时延迟/停止监听
            int emptyCount = 0;
            while (MAX_EMPTY_BATCH_COUNT < 0 || emptyCount <= MAX_EMPTY_BATCH_COUNT) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(BATCH_SIZE);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    Thread.sleep(EMPTY_BATCH_DELAY * 1000L);
                } else {
                    emptyCount = 0;
                    processData(message.getEntries());
                    log.info("Canal 成功处理 {} 条数据", message.getEntries().size());
                }
                // 确认并提交 ≤ batchId 的 Message
                connector.ack(batchId);
            }
            log.info("连续 {} 批次监听数据为空, 终止监听", MAX_EMPTY_BATCH_COUNT);
        } catch (Exception e) {
            log.error("Canal 监听数据报错: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } finally {
            connector.disconnect();
        }
    }

    /** 逐行处理数据 */
    private void processData(List<Entry> entries) {
        for (Entry entry : entries) {
            // 跳过默认事件
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChange;
            try {
                rowChange = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                log.error("Canal 解析数据失败, data: {}", entry, e);
                throw new UnsupportedOperationException("Canal 解析数据失败, data: " + entry, e);
            }

            EventType eventType = rowChange.getEventType();
            // 根据事件类型(增/删/改)采取不同措施
            for (RowData rowData : rowChange.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                    // 删除
                    afterEventDelete(entry.getHeader(), rowData);
                } else if (eventType == EventType.INSERT) {
                    // 新增
                    afterEventInsert(entry.getHeader(), rowData);
                } else if (eventType == EventType.UPDATE) {
                    // 修改
                    afterEventUpdate(entry.getHeader(), rowData);
                }
            }
        }
    }

    /** 生成 sql 语句并执行 */
    private void buildUpSqlThenExecute(Header header, RowData rowData, StringBuilder insertFields, StringBuilder insertValues) {
        for (Column column : rowData.getAfterColumnsList()) {
            insertFields.append(column.getName()).append(", ");
            if (StringUtils.isBlank(column.getValue())) {
                insertValues.append("null").append(", ");
            } else {
                insertValues.append("'").append(column.getValue()).append("', ");
            }
            log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
        insertFields.deleteCharAt(insertFields.lastIndexOf(","));
        insertValues.deleteCharAt(insertValues.lastIndexOf(","));
        insertFields.append(") ");
        insertValues.append("); ");

        // 提交至数据库
        String sql = insertFields + insertValues.toString();
        try {
            jdbcTemplate.update(sql);
        } catch (BadSqlGrammarException sqlException) {
            // 表不存在时需要先创建
            String sqlCopyTable = "create table " + backupSchema + backupPrefix + header.getTableName() + " like " + header.getSchemaName() + "." + header.getTableName();
            try {
                jdbcTemplate.update(sqlCopyTable);
                jdbcTemplate.update(sql);
            } catch (Exception e) {
                log.error("canal 数据插入/更新报错", e);
            }
        }
    }
}