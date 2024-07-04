package com.sgh.demo.sharding.database.service.impl;

import com.sgh.demo.sharding.database.db.entity.DemoEntitySharding;
import com.sgh.demo.sharding.database.db.repository.DemoEntityShardingRepository;
import com.sgh.demo.sharding.database.pojo.query.DemoEntityShardingQueryDto;
import com.sgh.demo.sharding.database.pojo.upsert.DemoEntityShardingUpsertDto;
import com.sgh.demo.sharding.database.service.DemoEntityShardingService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * [功能] 测试数据--分表
 *
 * @author Song gh
 * @version 2024/03/22
 */
@Service
public class DemoEntityShardingServiceImpl implements DemoEntityShardingService {

    @Resource
    private DemoEntityShardingRepository demoEntityShardingRepository;

    /** [新增/更新] 测试数据--分表 */
    @Override
    public void upsert(DemoEntityShardingUpsertDto dto) {
        // 仅当 id 存在且有效时更新数据, 否则新增数据
        Long id = dto.getId();
        if (id != null) {
            DemoEntitySharding optData = demoEntityShardingRepository.findFirstByIdAndIsDeletedIsFalse(id);
            if (optData != null) {
                // 数据有效, 更新并记录时间
                optData.update(dto);
                demoEntityShardingRepository.save(optData);
                return;
            }
        }
        // id 不存在或无效则新增
        demoEntityShardingRepository.save(new DemoEntitySharding(dto));
    }

    /** [删除] 测试数据--分表 */
    @Override
    public void delete(List<Long> idList) {
        demoEntityShardingRepository.markDeletedByIdList(idList);
    }

    /** [查询] 测试数据--分表 */
    @Override
    public DemoEntitySharding get(Long id) {
        return demoEntityShardingRepository.findFirstByIdAndIsDeletedIsFalse(id);
    }

    /** [列表查询] 测试数据--分表 */
    @Override
    public List<DemoEntitySharding> getList() {
        return demoEntityShardingRepository.findAllByIsDeletedIsFalse();
    }

    /** [分页查询] 测试数据--分表 */
    @Override
    public Page<DemoEntitySharding> getPage(DemoEntityShardingQueryDto dto) {
        return demoEntityShardingRepository.getPage(dto.getAesName(), PageRequest.of(dto.getPage() - 1, dto.getSize()));
    }

    /** [分页查询] 并表数据 */
    @Override
    public Page<Map<String, Object>> getMergedPage(DemoEntityShardingQueryDto dto) {
        return demoEntityShardingRepository.getMergedPage(dto);
    }

    /** [列表查询] 检查分片审计 */
    @Override
    public List<Map<String, Object>> checkShardingAuditor() {
        return demoEntityShardingRepository.checkAuditor();
    }
}
