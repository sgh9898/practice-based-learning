package com.sgh.demo.datasource.db.repository;

import com.sgh.demo.datasource.db.entity.MultiSrcDemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * [数据库交互] 测试数据
 *
 * @author Song gh
 * @version 2024/05/15
 */
public interface MultiSrcDemoEntityRepository extends JpaRepository<MultiSrcDemoEntity, Long> {
}
