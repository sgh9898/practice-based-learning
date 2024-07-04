package com.sgh.demo.datasource.db.repository;

import com.sgh.demo.datasource.db.entity.MultiSrcDemoEntitySharding;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * [数据库交互] 测试数据--分表
 *
 * @author Song gh
 * @version 2024/05/15
 */
public interface MultiSrcDemoEntityShardingRepository extends JpaRepository<MultiSrcDemoEntitySharding, Long> {
}
