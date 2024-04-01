package com.collin.demo.sharding.database.service;

import com.collin.demo.sharding.database.db.entity.DemoEntitySharding;
import com.collin.demo.sharding.database.pojo.query.DemoEntityShardingQueryDto;
import com.collin.demo.sharding.database.pojo.upsert.DemoEntityShardingUpsertDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * [功能] 测试数据--分表
 *
 * @author Song gh
 * @version 2024/03/22
 */
public interface DemoEntityShardingService {

    /** [新增/更新] 测试数据--分表 */
    void upsert(DemoEntityShardingUpsertDto dto);

    /** [删除] 测试数据--分表 */
    void delete(List<Long> idList);

    /** [查询] 测试数据--分表 */
    DemoEntitySharding get(Long id);

    /** [列表查询] 测试数据--分表 */
    List<DemoEntitySharding> getList();

    /** [分页查询] 测试数据--分表 */
    Page<DemoEntitySharding> getPage(DemoEntityShardingQueryDto dto);

    /** [分页查询] 并表数据 */
    Page<Map<String, Object>> getMergedPage(DemoEntityShardingQueryDto dto);

    /** [列表查询] 检查分片审计 */
    List<Map<String, Object>> checkShardingAuditor();
}
