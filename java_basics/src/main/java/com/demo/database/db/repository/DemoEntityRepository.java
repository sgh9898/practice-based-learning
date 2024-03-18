package com.demo.database.db.repository;

import com.demo.database.db.entity.DemoEntity;
import com.demo.database.pojo.excel.ExcelDemoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [数据库交互] 测试数据
 *
 * @author Song gh
 * @version 2024/03/18
 */
@Repository
public interface DemoEntityRepository extends JpaRepository<DemoEntity, Long> {

    /** [删除] 测试数据 */
    @Modifying
    @Transactional
    @Query("update DemoEntity set isDeleted = true, updateTime = now() " +
            "where id in (:idList) ")
    void markDeletedByIdList(@Param("idList") List<Long> idList);

    /** [查询] 测试数据 */
    DemoEntity findFirstByIdAndIsDeletedIsFalse(Long id);

    /** [列表] 测试数据 */
    List<DemoEntity> findAllByIsDeletedIsFalse();

    /** [分页] 测试数据 */
    Page<DemoEntity> findAllByIsDeletedIsFalse(Pageable pageable);

    /** [列表, excel] 测试数据 excel */
    @Query("select new com.demo.database.pojo.excel.ExcelDemoEntity(entity) " +
            "from DemoEntity as entity " +
            "where entity.isDeleted = false ")
    List<ExcelDemoEntity> getExcelList();
}
