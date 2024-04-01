package com.collin.demo.sharding.database.db.repository;

import com.collin.demo.sharding.database.db.entity.DemoEntityShardingCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * [数据库交互] 测试数据--分表--copy
 *
 * @author Song gh
 * @version 2024/03/22
 */
public interface DemoEntityShardingCopyRepository extends JpaRepository<DemoEntityShardingCopy, Long> {

    /** 测试数据--分表--copy */
    DemoEntityShardingCopy findFirstByIdAndIsDeletedIsFalse(Long id);

    /** 测试数据--分表--copy */
    List<DemoEntityShardingCopy> findAllByIsDeletedIsFalse();

    /** [分页查询] 测试数据--分表--copy */
    Page<DemoEntityShardingCopy> findAllByIsDeletedIsFalse(Pageable pageable);
}
