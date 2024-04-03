package com.sgh.demo.sharding.database.service.impl;

import com.sgh.demo.sharding.database.db.entity.DemoEntityShardingExtraCopy;
import com.sgh.demo.sharding.database.db.repository.DemoEntityShardingExtraCopyRepository;
import com.sgh.demo.sharding.database.pojo.query.DemoEntityShardingExtraCopyQueryDto;
import com.sgh.demo.sharding.database.pojo.upsert.DemoEntityShardingExtraCopyUpsertDto;
import com.sgh.demo.sharding.database.service.DemoEntityShardingExtraCopyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * [功能] 测试数据--分库分表
 *
 * @author Song gh
 * @version 2024/03/28
 */
@Service
public class DemoEntityShardingExtraCopyServiceImpl implements DemoEntityShardingExtraCopyService {

    @Resource
    private DemoEntityShardingExtraCopyRepository demoEntityShardingExtraCopyRepository;

    /** [新增/更新] 测试数据--分库分表 */
    @Override
    public void upsert(DemoEntityShardingExtraCopyUpsertDto dto) {
        // 仅当 id 存在且有效时更新数据, 否则新增数据
        Long id = dto.getId();
        if (id != null) {
            DemoEntityShardingExtraCopy optData = demoEntityShardingExtraCopyRepository.findFirstByIdAndIsDeletedIsFalse(id);
            if (optData != null) {
                // 数据有效, 更新并记录时间
                optData.update(dto);
                demoEntityShardingExtraCopyRepository.save(optData);
                return;
            }
        }
        // id 不存在或无效则新增
        demoEntityShardingExtraCopyRepository.save(new DemoEntityShardingExtraCopy(dto));
    }

    /** [删除] 测试数据--分库分表 */
    @Override
    public void delete(List<Long> idList) {
        demoEntityShardingExtraCopyRepository.markDeletedByIdList(idList);
    }

    /** 测试数据--分库分表 */
    @Override
    public DemoEntityShardingExtraCopy get(Long id) {
        return demoEntityShardingExtraCopyRepository.findFirstByIdAndIsDeletedIsFalse(id);
    }

    /** [列表] 查询测试数据--分库分表 */
    @Override
    public List<DemoEntityShardingExtraCopy> getList() {
        return demoEntityShardingExtraCopyRepository.findAllByIsDeletedIsFalse();
    }

    /** [分页] 查询测试数据--分库分表 */
    @Override
    public Page<DemoEntityShardingExtraCopy> getPage(DemoEntityShardingExtraCopyQueryDto dto) {
        return demoEntityShardingExtraCopyRepository.findAllByIsDeletedIsFalse(PageRequest.of(dto.getPage() - 1, dto.getSize()));
    }
}
