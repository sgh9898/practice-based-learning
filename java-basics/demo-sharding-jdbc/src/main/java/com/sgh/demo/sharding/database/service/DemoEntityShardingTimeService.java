package com.sgh.demo.sharding.database.service;

import com.sgh.demo.sharding.database.db.entity.DemoEntityShardingTime;
import com.sgh.demo.sharding.database.pojo.query.DemoEntityShardingTimeQueryDto;
import com.sgh.demo.sharding.database.pojo.upsert.DemoEntityShardingTimeUpsertDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * [功能] 测试数据--时间分表
 *
 * @author Song gh
 * @version 2024/03/28
 */
public interface DemoEntityShardingTimeService {

    /** [新增/更新] 测试数据--时间分表 */
    void upsert(DemoEntityShardingTimeUpsertDto dto);

    /** [删除] 测试数据--时间分表 */
    void delete(List<Long> idList);

    /** 查询测试数据--时间分表 */
    DemoEntityShardingTime get(Long id);

    /** [列表] 查询测试数据--时间分表 */
    List<DemoEntityShardingTime> getList();

    /** [分页] 查询测试数据--时间分表 */
    Page<DemoEntityShardingTime> getPage(DemoEntityShardingTimeQueryDto dto);
}
