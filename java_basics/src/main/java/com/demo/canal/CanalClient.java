package com.demo.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Canal Client 功能
 *
 * @author Song gh on 2023/5/11.
 */
@Slf4j
@Component
public class CanalClient {

// ------------------------------ 数据库参数 ------------------------------
    /** 单批次处理的数据量 */
    private static final Integer BATCH_SIZE = 1000;
    /** 连续数据为空的最大批次, 到达计数时 canal 停止运行, 仅在 ≥ 0 时生效 */
    private static final Integer MAX_EMPTY_COUNT = -1;
    /** 当前批次数据为空后延迟 x 秒再执行 */
    private static final Integer EMPTY_COUNT_DELAY = 5;

// ---------- Canal Deployer 参数(需要匹配 Canal Deployer 部署参数, 不可仅在项目内修改) ----------
    /** canal 使用的数据库账号: 用户名 */
    @Value("${canal.username:canal}")
    private String canalUsername;
    /** canal 使用的数据库账号: 密码 */
    @Value("${canal.password:canal}")
    private String canalPassword;
    /**
     * canal 监听的表, 未配置时以 canal deployer 为准
     * 监听全表             .*\..*
     * 监听指定 schema      test\..*
     * 监听指定表            test.table1
     * 综合监听(逗号分隔)     test1\..*,test2.table1,test2.table2
     */
    @Value("${canal.subscribe:.*\\..*}")
    private String subscribeRegex;

// ------------------------------ 运行参数 ------------------------------
    /** 部署 canal 的 host, 默认为本机 localhost */
    @Value("${canal.host:localhost}")
    private String canalHost;
    /** canal 所使用的端口(canal.properties 中 canal.port), 默认 11111 */
    @Value("${canal.port:11111}")
    private Integer canalPort;
    /** canal instance 名称, 默认为 example */
    @Value("${canal.port:example}")
    private String canalInstance;

// ============================== 常用参数 End ==============================

// ------------------------------ 业务参数 ------------------------------
    /** 备份表所属 schema */
    @Value("${canal.extension.slave.schema:}")
    private String BACKUP_SCHEMA;

    /** 备份表统一前缀 */
    @Value("${canal.extension.slave.table-name-prefix:}")
    private String BACKUP_PREFIX;
// ============================== 业务参数 End ==============================

    @Resource
    private JdbcTemplate jdbcTemplate;

    /** 需要在项目运行时启动 */
// todo   @PostConstruct
    public void start() {
        log.info("canal 开始运行");
        // 链接数据库
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalHost,
                canalPort), canalInstance, canalUsername, canalPassword);
        connector.connect();
        // 订阅的数据库表, 未配置时以 canal deployer 为准
        if (StringUtils.isNotBlank(subscribeRegex)) {
            connector.subscribe(subscribeRegex);
        } else {
            connector.subscribe();
        }
        // 回滚至未进行 ack 的地方
        connector.rollback();

        // 处理从表信息
        if (StringUtils.isNotBlank(BACKUP_SCHEMA) && !BACKUP_SCHEMA.endsWith(".")) {
            BACKUP_SCHEMA += ".";
        }
        if (StringUtils.isNotBlank(BACKUP_PREFIX) && !BACKUP_PREFIX.endsWith("_")) {
            BACKUP_PREFIX += "_";
        }
        // 进行数据监控
        new Thread(() -> dataMonitoring(connector)).start();
    }

// ------------------------------ 数据监控主方法 ------------------------------

    /**
     * 监听到数据删除时进行的处理
     *
     * @param header  数据库, 事件类型等信息
     * @param rowData 具体数据
     */
    private void afterEventDelete(Header header, RowData rowData) {
    }

    /**
     * 监听到数据插入时进行的处理
     *
     * @param header  数据库, 事件类型等信息
     * @param rowData 具体数据
     */
    private void afterEventInsert(Header header, RowData rowData) {
        // 生成 sql 语句
        StringBuilder insertFields = new StringBuilder("insert into " + BACKUP_SCHEMA + BACKUP_PREFIX + header.getTableName() + " (");
        StringBuilder insertValues = new StringBuilder("values (");
        buildUpSqlThenExecute(header, rowData, insertFields, insertValues);
    }

    /**
     * 监听到数据更新时进行的处理
     *
     * @param header  数据库, 事件类型等信息
     * @param rowData 具体数据
     */
    private void afterEventUpdate(Header header, RowData rowData) {
        // 生成 sql 语句
        StringBuilder insertFields = new StringBuilder("replace into " + BACKUP_SCHEMA + BACKUP_PREFIX + header.getTableName() + " (");
        StringBuilder insertValues = new StringBuilder("values (");
        buildUpSqlThenExecute(header, rowData, insertFields, insertValues);
    }

// ============================== 数据监控主方法 End ==============================

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
            String sqlCopyTable = "create table " + BACKUP_SCHEMA + BACKUP_PREFIX + header.getTableName() + " like " + header.getSchemaName() + "." + header.getTableName();
            try {
                jdbcTemplate.update(sqlCopyTable);
                jdbcTemplate.update(sql);
            } catch (Exception e) {
                log.error("canal 数据插入/更新报错", e);
            }
        }
    }

    /** canal 监听数据 */
    private void dataMonitoring(CanalConnector connector) {
        try {
            // 持续监听数据库, 连续为空时延迟/停止监听
            int emptyCount = 0;
            while (MAX_EMPTY_COUNT < 0 || emptyCount <= MAX_EMPTY_COUNT) {
                Message message = connector.getWithoutAck(BATCH_SIZE); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    Thread.sleep(EMPTY_COUNT_DELAY * 1000);
                } else {
                    emptyCount = 0;
                    dealWithOneLineEntry(message.getEntries());
                }
                // 确认并提交 ≤ batchId 的 Message
                connector.ack(batchId); // 提交确认
            }
            log.info("连续 {} 批次监听数据为空, 终止监听", MAX_EMPTY_COUNT);
        } catch (Exception e) {
            log.error("监听数据报错", e);
            throw new RuntimeException(e);
        } finally {
            connector.disconnect();
        }
    }

    /** 处理单行数据 */
    private void dealWithOneLineEntry(List<Entry> entries) {
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
                throw new RuntimeException("Canal 解析数据失败, data: " + entry, e);
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

    /** 默认处理方法: 删除数据 */
    private void defaultAfterEventDelete(Header header, RowData rowData) {
        log.info("Canal 读取记录: binlog[{}:{}] , name[{},{}] , eventType : DELETE\n",
                header.getLogfileName(), header.getLogfileOffset(), header.getSchemaName(), header.getTableName());
        for (Column column : rowData.getBeforeColumnsList()) {
            log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    /** 默认处理方法: 插入数据 */
    private void defaultAfterEventInsert(Header header, RowData rowData) {
        log.info("Canal 读取记录: binlog[{}:{}] , name[{},{}] , eventType : INSERT\n",
                header.getLogfileName(), header.getLogfileOffset(), header.getSchemaName(), header.getTableName());
        for (Column column : rowData.getAfterColumnsList()) {
            log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    /** 默认处理方法: 更新数据 */
    private void defaultAfterEventUpdate(Header header, RowData rowData) {
        log.info("Canal 读取记录: binlog[{}:{}] , name[{},{}] , eventType : UPDATE\n",
                header.getLogfileName(), header.getLogfileOffset(), header.getSchemaName(), header.getTableName());
        for (Column column : rowData.getBeforeColumnsList()) {
            log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
        for (Column column : rowData.getAfterColumnsList()) {
            log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
}