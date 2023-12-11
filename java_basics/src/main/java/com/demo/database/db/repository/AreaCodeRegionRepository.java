package com.demo.database.db.repository;

import com.demo.database.db.entity.AreaCodeRegion;
import com.demo.database.pojo.excel.ExcelAreaCodeRegion;
import com.demo.database.pojo.vo.AreaCodeRegionVo;
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
 * @author Song gh on 2023/12/04.
 */
@Repository
public interface AreaCodeRegionRepository extends JpaRepository<AreaCodeRegion, Integer>, AreaCodeRegionRepositoryCustom {

    /** [删除] 区号 */
    @Modifying
    @Transactional
    @Query("update AreaCodeRegion set isDeleted = true, updateTime = now() " +
            "where id in (:idList) ")
    void markDeletedByIdList(@Param("idList") List<Integer> idList);

    /** [查询] 区号 */
    AreaCodeRegion findFirstByIdAndIsDeletedIsFalse(Integer id);

    /** [列表] 区号 */
    List<AreaCodeRegion> findAllByIsDeletedIsFalse();

    /**
     * todo
     * Sql 使用 list 查询
     * (list 不允许做 is null 判断, 但是可以用另一个参数辅助)
     */
    @Query(value = "SELECT * FROM area_code_region " +
            "WHERE ((:needList IS FALSE) OR (:needList IS TRUE AND id IN :idList)) ", nativeQuery = true)
    List<AreaCodeRegion> sqlGetByList(@Param("needList") Boolean needList, @Param("idList") List<Long> idList);

    /**
     * todo
     * Hql 使用 list 查询
     *
     * @param idList 不允许做 is null 判断
     */
    @Query("select new com.demo.database.pojo.vo.AreaCodeRegionVo(region) " +
            "from AreaCodeRegion as region " +
            "where region.id in (:idList) ")
    List<AreaCodeRegionVo> hqlGetByList(@Param("idList") List<Long> idList);

    /** [分页] 区号 */
    Page<AreaCodeRegion> findAllByIsDeletedIsFalse(Pageable pageable);

    /** [列表, excel] 区号 excel */
    @Query("select new com.demo.database.pojo.excel.ExcelAreaCodeRegion(region) " +
            "from AreaCodeRegion as region " +
            "where region.isDeleted = false ")
    List<ExcelAreaCodeRegion> getExcelList();
}

/** 自定义 repository, 用于不便使用 jpa 的场景 */
interface AreaCodeRegionRepositoryCustom {

    /** [查询] 获取 vo */
    AreaCodeRegionVo getSingleVo(String districtName);
}

