package com.demo.database.repository;

import com.demo.database.entity.DemoEntity;
import com.demo.database.pojo.DemoEntityVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 演示类 Repository
 *
 * @author Song gh on 2022/04/14.
 */
@Repository
public interface DemoEntityRepository extends JpaRepository<DemoEntity, Long>, DemoEntityCustom {

    /** 批量逻辑删除, 同时标记更新时间 */
    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update demo_entity " +
                    "set is_deleted = true, " +
                    "    update_time = now() " +
                    "where id in :idList ")
    void markDeletedByIdList(@Param("idList") List<Long> idList);

    /** 单查询 */
    DemoEntity findFirstByIdAndIsDeletedIsFalse(Long id);

    /** 列表查询 */
    List<DemoEntity> findAllByIsDeletedIsFalse();

    /** 分页查询 */
    Page<DemoEntity> findAllByIsDeletedIsFalse(Pageable pageable);

//    /** Excel 列表 */
//    @Query(value = "select new ExcelDemoEntity(data) " +
//            "from DemoEntity as data " +
//            "where data.isDeleted = false " +
//            "   and ")
//    List<ExcelDemoEntity> getExcelList(@Param(""));

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
    @Query("select new com.demo.database.pojo.DemoEntityVo(demo) from DemoEntity as demo " +
            "where demo.id in (:idList) ")
    List<DemoEntity> hqlGetByList(@Param("idList") List<Long> idList);

    @Query(nativeQuery = true, value = "select * from demo_entity where name = :name")
    Page<Map<String, Object>> findAllByName(@Param("name") String name, Pageable pageable);
}

interface DemoEntityCustom {

    DemoEntityVo getSingleVo(String name);
}
