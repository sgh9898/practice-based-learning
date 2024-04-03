package com.sgh.demo.sharding.database.db.repository;

import com.sgh.demo.sharding.database.db.entity.DemoEntitySharding;
import com.sgh.demo.sharding.database.pojo.query.DemoEntityShardingQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * [数据库交互] 测试数据--分表
 *
 * @author Song gh
 * @version 2024/03/22
 */
@Repository
public interface DemoEntityShardingRepository extends JpaRepository<DemoEntitySharding, Long>, DemoEntityShardingRepositoryCustom {

    /** [删除] 测试数据--分表 */
    @Modifying
    @Transactional
    @Query("update DemoEntitySharding set isDeleted = true, updateTime = now() " +
            "where id in (:idList) ")
    void markDeletedByIdList(@Param("idList") List<Long> idList);

    /** 测试数据--分表 */
    DemoEntitySharding findFirstByIdAndIsDeletedIsFalse(Long id);

    /** 测试数据--分表 */
    List<DemoEntitySharding> findAllByIsDeletedIsFalse();

    /** [分页查询] 测试数据--分表 */
    @Query(value = "SELECT * FROM demo_entity_sharding WHERE aes_name = :aesName ", nativeQuery = true)
    Page<DemoEntitySharding> getPage(@Param("aesName") String aesName, Pageable pageable);
}

interface DemoEntityShardingRepositoryCustom {

    /** [分页查询] 并表数据 */
    Page<Map<String, Object>> getMergedPage(DemoEntityShardingQueryDto dto);

    /** 检查分片审计 */
    List<Map<String, Object>> checkAuditor();
}
