package com.collin.demo.sharding.database.db.repository;

import com.collin.demo.sharding.database.db.entity.DictData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * [数据库交互] 数据字典
 *
 * @author Song gh
 * @version 2024/03/27
 */
@Repository
public interface DictDataRepository extends JpaRepository<DictData, Long> {

    /** 查询数据字典 */
    DictData findFirstById(Long id);
}
