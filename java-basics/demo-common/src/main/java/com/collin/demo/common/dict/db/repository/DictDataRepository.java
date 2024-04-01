package com.collin.demo.common.dict.db.repository;

import com.collin.demo.common.dict.db.entity.DictData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * [数据库交互] 数据字典
 *
 * @author Song gh
 * @version 2024/03/12
 */
@Repository
public interface DictDataRepository extends JpaRepository<DictData, Long> {
}
