package com.sgh.demo.sharding.database.service.impl;

import com.sgh.demo.sharding.database.db.entity.DemoEntityShardingTime;
import com.sgh.demo.sharding.database.db.repository.DemoEntityShardingTimeRepository;
import com.sgh.demo.sharding.database.pojo.query.DemoEntityShardingTimeQueryDto;
import com.sgh.demo.sharding.database.pojo.upsert.DemoEntityShardingTimeUpsertDto;
import com.sgh.demo.sharding.database.service.DemoEntityShardingTimeService;
import com.sgh.demo.sharding.sharding.util.ShardingDateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * [功能] 测试数据--时间分表
 *
 * @author Song gh
 * @version 2024/03/28
 */
@Service
public class DemoEntityShardingTimeServiceImpl implements DemoEntityShardingTimeService {

    @Resource
    private DemoEntityShardingTimeRepository demoEntityShardingTimeRepository;

    /** [新增/更新] 测试数据--时间分表 */
    @Override
    public void upsert(DemoEntityShardingTimeUpsertDto dto) {
        // 仅当 id 存在且有效时更新数据, 否则新增数据
        Long id = dto.getId();
        if (id != null) {
            DemoEntityShardingTime optData = demoEntityShardingTimeRepository.findFirstByIdAndIsDeletedIsFalse(id);
            if (optData != null) {
                // 数据有效, 更新并记录时间
                optData.update(dto);
                demoEntityShardingTimeRepository.save(optData);
                return;
            }
        }
        // id 不存在或无效则新增
        demoEntityShardingTimeRepository.save(new DemoEntityShardingTime(dto));
    }

    /** [删除] 测试数据--时间分表 */
    @Override
    public void delete(List<Long> idList) {
        demoEntityShardingTimeRepository.markDeletedByIdList(idList);
    }

    /** 测试数据--时间分表 */
    @Override
    public DemoEntityShardingTime get(Long id) {
        return demoEntityShardingTimeRepository.findFirstByIdAndIsDeletedIsFalse(id);
    }

    /** [列表] 查询测试数据--时间分表 */
    @Override
    public List<DemoEntityShardingTime> getList() {
        return demoEntityShardingTimeRepository.findAllByIsDeletedIsFalseAndCreateTimeBefore(ShardingDateUtils.getDate(new Date(), 1, ChronoUnit.MONTHS));
    }

    /** [分页] 查询测试数据--时间分表 */
    @Override
    public Page<DemoEntityShardingTime> getPage(DemoEntityShardingTimeQueryDto dto) {
        return demoEntityShardingTimeRepository.findAllByIsDeletedIsFalse(PageRequest.of(dto.getPage() - 1, dto.getSize()));
    }
}
