package com.sgh.demo.common.database.service;

import com.sgh.demo.common.database.db.entity.DemoEntity;
import com.sgh.demo.common.database.pojo.query.DemoEntityQueryDto;
import com.sgh.demo.common.database.pojo.upsert.DemoEntityUpsertDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * [功能类] 测试数据
 *
 * @author Song gh
 * @version 2024/03/18
 */
public interface DemoEntityService {

    /** [新增/更新] 测试数据 */
    void upsert(DemoEntityUpsertDto dto);

    /** [删除] 测试数据 */
    void delete(List<Long> idList);

    /** [查询] 测试数据 */
    DemoEntity get(Long id);

    /** [列表] 测试数据 */
    List<DemoEntity> getList();

    /** [分页] 测试数据 */
    Page<DemoEntity> getPage(DemoEntityQueryDto dto);
}
