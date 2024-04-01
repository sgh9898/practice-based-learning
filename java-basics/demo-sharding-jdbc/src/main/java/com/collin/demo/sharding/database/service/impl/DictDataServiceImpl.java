package com.collin.demo.sharding.database.service.impl;

import com.collin.demo.sharding.database.db.entity.DictData;
import com.collin.demo.sharding.database.db.repository.DictDataRepository;
import com.collin.demo.sharding.database.pojo.upsert.DictDataUpsertDto;
import com.collin.demo.sharding.database.service.DictDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * [功能] 数据字典
 *
 * @author Song gh
 * @version 2024/03/27
 */
@Service
public class DictDataServiceImpl implements DictDataService {

    @Resource
    private DictDataRepository dictDataRepository;

    /** [新增/更新] 数据字典 */
    @Override
    public void upsert(DictDataUpsertDto dto) {
        // 仅当 id 存在且有效时更新数据, 否则新增数据
        Long id = dto.getId();
        if (id != null) {
            DictData optData = dictDataRepository.findFirstById(id);
            if (optData != null) {
                // 数据有效, 更新并记录时间
                optData.update(dto);
                dictDataRepository.save(optData);
                return;
            }
        }
        // id 不存在或无效则新增
        dictDataRepository.save(new DictData(dto));
    }
}
