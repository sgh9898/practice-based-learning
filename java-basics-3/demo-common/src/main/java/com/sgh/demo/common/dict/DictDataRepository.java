package com.sgh.demo.common.dict;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * [数据库交互] 数据字典
 *
 * @author Song gh
 * @version 2024/03/12
 */
public interface DictDataRepository extends JpaRepository<DictData, Long> {
}
