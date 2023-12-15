package com.demo.database.db.repository;

import com.demo.database.db.entity.Region;
import com.demo.database.pojo.excel.ExcelRegion;
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
 * [数据库交互] 区号
 *
 * @author Song gh on 2023/12/15.
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {

    /** [删除] 区号 */
    @Modifying
    @Transactional
    @Query("update Region set isDeleted = true, updateTime = now() " +
            "where id in (:idList) ")
    void markDeletedByIdList(@Param("idList") List<Integer> idList);

    /** [查询] 区号 */
    Region findFirstByIdAndIsDeletedIsFalse(Integer id);

    /** [列表] 区号 */
    List<Region> findAllByIsDeletedIsFalse();

    /** [分页] 区号 */
    Page<Region> findAllByIsDeletedIsFalse(Pageable pageable);

    /** [列表, excel] 区号 excel */
    @Query("select new com.demo.database.pojo.excel.ExcelRegion(entity) " +
            "from Region as entity " +
            "where entity.isDeleted = false ")
    List<ExcelRegion> getExcelList();
}

