package com.collin.demo.sharding.database.service;

import com.collin.demo.sharding.database.db.entity.DemoEntityShardingExtraCopy;
import com.collin.demo.sharding.database.pojo.query.DemoEntityShardingExtraCopyQueryDto;
import com.collin.demo.sharding.database.pojo.upsert.DemoEntityShardingExtraCopyUpsertDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * [功能] 测试数据--分库分表
 *
 * @author Song gh
 * @version 2024/03/28
 */
public interface DemoEntityShardingExtraCopyService {

    /** [新增/更新] 测试数据--分库分表 */
    void upsert(DemoEntityShardingExtraCopyUpsertDto dto);

    /** [删除] 测试数据--分库分表 */
    void delete(List<Long> idList);

    /** 查询测试数据--分库分表 */
    DemoEntityShardingExtraCopy get(Long id);

    /** [列表] 查询测试数据--分库分表 */
    List<DemoEntityShardingExtraCopy> getList();

    /** [分页] 查询测试数据--分库分表 */
    Page<DemoEntityShardingExtraCopy> getPage(DemoEntityShardingExtraCopyQueryDto dto);
}
