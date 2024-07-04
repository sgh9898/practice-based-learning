package com.sgh.demo.sharding.database.service;

import com.sgh.demo.sharding.database.db.entity.DemoEntityShardingCopy;
import com.sgh.demo.sharding.database.pojo.query.DemoEntityShardingCopyQueryDto;
import com.sgh.demo.sharding.database.pojo.upsert.DemoEntityShardingCopyUpsertDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * [功能] 测试数据--分表--copy
 *
 * @author Song gh
 * @version 2024/03/22
 */
public interface DemoEntityShardingCopyService {

    /** [新增/更新] 测试数据--分表--copy */
    void upsert(DemoEntityShardingCopyUpsertDto dto);

    /** [删除] 测试数据--分表--copy */
    void delete(List<Long> idList);

    /** [查询] 测试数据--分表--copy */
    DemoEntityShardingCopy get(Long id);

    /** [列表查询] 测试数据--分表--copy */
    List<DemoEntityShardingCopy> getList();

    /** [分页查询] 测试数据--分表--copy */
    Page<DemoEntityShardingCopy> getPage(DemoEntityShardingCopyQueryDto dto);
}
