package com.collin.demo.sharding.sharding.algorithm.table;

import com.collin.demo.common.util.DateUtils;
import com.collin.demo.sharding.sharding.util.ShardingDateUtils;
import com.collin.demo.sharding.sharding.util.ShardingJdbcUtils;
import com.google.common.collect.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.springframework.lang.NonNull;

import java.util.*;

/**
 * [Sharding 分片算法] 根据时间分表
 * <pre>
 * 1. Sharding 5.3 移除了 Spring 相关功能, 因此无法直接读取 DataSource, 需要手动指定, 数据源配置见 {@link ShardingJdbcUtils}
 * 2. 此算法可以对分片进行动态管理, 支持自动建表
 * 3. 使用此算法时, 要求: 实际表名 = 逻辑表名 + 分隔符(可以为空) + 分表后缀 </pre>
 *
 * @author Song gh
 * @version 2024/3/22
 */
public class TimeShardingAlgorithm implements StandardShardingAlgorithm<Date> {

    /** 逻辑表名与分表后缀的分隔符 */
    private static final String TABLE_SHARDING_SEPARATOR = "_";

    /** 分表后缀使用的日期格式, 如: yyyyMM */
    private static final String TABLE_SHARDING_PATTERN = "yyyyMM";

    /**
     * 精确匹配实际表
     *
     * @param availableTargetNames 当前逻辑表名对应的实际表名(对此参数的修改在程序运行时全程生效)
     * @param preciseShardingValue 当前查询参数
     */
    @Override
    public String doSharding(@NonNull Collection<String> availableTargetNames, PreciseShardingValue<Date> preciseShardingValue) {
        Date date = preciseShardingValue.getValue();
        String logicTableName = preciseShardingValue.getLogicTableName();
        String actualTableName = logicTableName + TABLE_SHARDING_SEPARATOR + DateUtils.toStr(date, TABLE_SHARDING_PATTERN);

        // 初始化实际表
        initAvailableTables(availableTargetNames, logicTableName);

        // 不存在则创建表
        if (!availableTargetNames.contains(actualTableName)) {
            ShardingJdbcUtils.createTable(actualTableName, logicTableName);
            availableTargetNames.add(actualTableName);
        }
        return actualTableName;
    }

    /**
     * 范围匹配实际表
     *
     * @param availableTargetNames 当前逻辑表名对应的实际表名(对此参数的修改在程序运行时全程生效)
     * @param rangeShardingValue   当前查询参数
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Date> rangeShardingValue) {
        String logicTableName = rangeShardingValue.getLogicTableName();

        // 初始化实际表
        initAvailableTables(availableTargetNames, logicTableName);

        // 获取查询范围的最大值, 最小值; 未指定时使用已有分表的最大值或最小值
        Range<Date> valueRange = rangeShardingValue.getValueRange();
        Date startDate = valueRange.hasLowerBound() ? valueRange.lowerEndpoint() : getLowerEndpoint(availableTargetNames);
        Date endDate = valueRange.hasUpperBound() ? valueRange.upperEndpoint() : getUpperEndpoint(availableTargetNames);
        String minTableName = logicTableName + TABLE_SHARDING_SEPARATOR + DateUtils.toStr(startDate, TABLE_SHARDING_PATTERN);
        String maxTableName = logicTableName + TABLE_SHARDING_SEPARATOR + DateUtils.toStr(endDate, TABLE_SHARDING_PATTERN);

        List<String> resultTableNameList = new LinkedList<>();

        for (String targetNames : availableTargetNames) {
            if (targetNames.compareToIgnoreCase(maxTableName) <= 0 &&
                    targetNames.compareToIgnoreCase(minTableName) >= 0) {
                resultTableNameList.add(targetNames);
            }
        }
        return resultTableNameList;
    }

// ------------------------------ Private 方法 ------------------------------

    /**
     * 获取最小分片值
     *
     * @param tableNames 表名集合
     * @return 最小分片值
     */
    private Date getLowerEndpoint(Collection<String> tableNames) {
        if (tableNames == null || tableNames.isEmpty()) {
            return ShardingDateUtils.from("202401", TABLE_SHARDING_PATTERN);
        }
        String minTableName = Collections.min(tableNames);
        if (StringUtils.isNotBlank(minTableName)) {
            String dateStr = minTableName.substring(minTableName.lastIndexOf(TABLE_SHARDING_SEPARATOR) + 1);
            return ShardingDateUtils.from(dateStr, TABLE_SHARDING_PATTERN);
        } else {
            throw new IllegalArgumentException("获取最小分片值失败, 需要存在至少一张表");
        }
    }

    /**
     * 获取 最大分片值
     *
     * @param tableNames 表名集合
     * @return 最大分片值
     */
    private Date getUpperEndpoint(Collection<String> tableNames) {
        if (tableNames == null || tableNames.isEmpty()) {
            return ShardingDateUtils.from("202501", TABLE_SHARDING_PATTERN);
        }
        String maxTableName = Collections.max(tableNames);
        if (StringUtils.isNotBlank(maxTableName)) {
            String dateStr = maxTableName.substring(maxTableName.lastIndexOf("_"));
            return ShardingDateUtils.from(dateStr, TABLE_SHARDING_PATTERN);
        } else {
            throw new IllegalArgumentException("获取最大分片值失败");
        }
    }

    /**
     * 初始化实际表(因动态分表, 初次读取时需要加载实际表名)
     *
     * @param availableTargetNames 当前读取到的实际表名
     * @param logicTableName       逻辑表名, 要求实际表名 = 逻辑表名 + 分隔符 + 分表后缀
     */
    private static void initAvailableTables(Collection<String> availableTargetNames, String logicTableName) {
        if (availableTargetNames.size() == 1 && availableTargetNames.contains(logicTableName)) {
            List<String> allTableNameBySchema = ShardingJdbcUtils.getActualTables(logicTableName, TABLE_SHARDING_SEPARATOR);
            availableTargetNames.clear();
            availableTargetNames.addAll(allTableNameBySchema);
        }
    }
}
