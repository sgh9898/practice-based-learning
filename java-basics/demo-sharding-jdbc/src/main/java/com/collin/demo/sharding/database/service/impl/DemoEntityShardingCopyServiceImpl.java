package com.collin.demo.sharding.database.service.impl;

import com.collin.demo.sharding.database.db.entity.DemoEntityShardingCopy;
import com.collin.demo.sharding.database.db.repository.DemoEntityShardingCopyRepository;
import com.collin.demo.sharding.database.pojo.query.DemoEntityShardingCopyQueryDto;
import com.collin.demo.sharding.database.pojo.upsert.DemoEntityShardingCopyUpsertDto;
import com.collin.demo.sharding.database.service.DemoEntityShardingCopyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * [功能] 测试数据--分表--copy
 *
 * @author Song gh
 * @version 2024/03/22
 */
@Service
public class DemoEntityShardingCopyServiceImpl implements DemoEntityShardingCopyService {

    @Resource
    private DemoEntityShardingCopyRepository demoEntityShardingCopyRepository;

    /** [新增/更新] 测试数据--分表--copy */
    @Override
    public void upsert(DemoEntityShardingCopyUpsertDto dto) {
        // 仅当 id 存在且有效时更新数据, 否则新增数据
        Long id = dto.getId();
        if (id != null) {
            DemoEntityShardingCopy optData = demoEntityShardingCopyRepository.findFirstByIdAndIsDeletedIsFalse(id);
            if (optData != null) {
                // 数据有效, 更新并记录时间
                optData.update(dto);
                demoEntityShardingCopyRepository.save(optData);
                return;
            }
        }
        // id 不存在或无效则新增
        demoEntityShardingCopyRepository.save(new DemoEntityShardingCopy(dto));
    }

    /** [删除] 测试数据--分表--copy */
    @Override
    public void delete(List<Long> idList) {
//        demoEntityShardingCopyRepository.markDeletedByIdList(idList);
    }

    /** [查询] 测试数据--分表--copy */
    @Override
    public DemoEntityShardingCopy get(Long id) {
        return demoEntityShardingCopyRepository.findFirstByIdAndIsDeletedIsFalse(id);
    }

    /** [列表查询] 测试数据--分表--copy */
    @Override
    public List<DemoEntityShardingCopy> getList() {
        return demoEntityShardingCopyRepository.findAllByIsDeletedIsFalse();
    }

    /** [分页查询] 测试数据--分表--copy */
    @Override
    public Page<DemoEntityShardingCopy> getPage(DemoEntityShardingCopyQueryDto dto) {
        return demoEntityShardingCopyRepository.findAllByIsDeletedIsFalse(PageRequest.of(dto.getPage() - 1, dto.getSize()));
    }
}
