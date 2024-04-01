package com.collin.demo.sharding.database.service;

import com.collin.demo.sharding.database.pojo.upsert.DictDataUpsertDto;

/**
 * [功能] 数据字典
 *
 * @author Song gh
 * @version 2024/03/27
 */
public interface DictDataService {

    /** [新增/更新] 数据字典 */
    void upsert(DictDataUpsertDto dto);
}
