package com.collin.demo.sharding.database.db.repository;

import com.collin.demo.sharding.database.db.entity.DemoEntityShardingTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * [数据库交互] 测试数据--时间分表
 *
 * @author Song gh
 * @version 2024/03/28
 */
@Repository
public interface DemoEntityShardingTimeRepository extends JpaRepository<DemoEntityShardingTime, Long> {

    /** [删除] 测试数据--时间分表 */
    @Modifying
    @Transactional
    @Query("update DemoEntityShardingTime set isDeleted = true, updateTime = now() " +
            "where id in (:idList) ")
    void markDeletedByIdList(@Param("idList") List<Long> idList);

    /** 查询测试数据--时间分表 */
    DemoEntityShardingTime findFirstByIdAndIsDeletedIsFalse(Long id);

    /** [列表] 查询测试数据--时间分表 */
    List<DemoEntityShardingTime> findAllByIsDeletedIsFalseAndCreateTimeBefore(Date date);

    /** [分页] 查询测试数据--时间分表 */
    Page<DemoEntityShardingTime> findAllByIsDeletedIsFalse(Pageable pageable);
}
