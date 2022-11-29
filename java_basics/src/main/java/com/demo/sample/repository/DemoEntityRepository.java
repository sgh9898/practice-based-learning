package com.demo.sample.repository;

import com.demo.sample.entity.DemoEntity;
import com.demo.sample.pojo.DemoEntityVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 演示类 Repository
 *
 * @author Song gh on 2022/04/14.
 */
public interface DemoEntityRepository extends JpaRepository<DemoEntity, Long>, DemoEntityCustom {

    Optional<DemoEntity> findFirstByNameAndIsDeletedIsFalse(String name);

    /**
     * Sql 使用 list 查询
     *
     * @param idList 不允许做 is null 判断
     */
    @Query(nativeQuery = true, value = "select * from demo_entity " +
            "where id in :idList")
    List<DemoEntity> sqlGetByList(@Param("idList") List<Long> idList);

    /**
     * Hql 使用 list 查询
     *
     * @param idList 不允许做 is null 判断
     */
    @Query("select new com.demo.sample.entity.DemoEntity(demo) from DemoEntity as demo " +
            "where demo.id in (:idList) ")
    List<DemoEntity> hqlGetByList(@Param("idList") List<Long> idList);

    @Query(nativeQuery = true, value = "select * from demo_entity where name = :name")
    Page<Map<String, Object>> findAllByName(@Param("name") String name, Pageable pageable);
}

interface DemoEntityCustom {

    DemoEntityVo getSingleVo(String name);
}
