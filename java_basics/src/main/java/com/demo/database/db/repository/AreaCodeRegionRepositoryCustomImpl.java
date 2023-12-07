package com.demo.database.db.repository;

import com.demo.database.pojo.vo.AreaCodeRegionVo;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * 区号
 * (自定义 repository, 用于不便使用 jpa 的场景)
 *
 * @author Song gh on 2023/12/4.
 */
@Service
public class AreaCodeRegionRepositoryCustomImpl implements AreaCodeRegionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /** [查询] 获取 vo */
    @Override
    public AreaCodeRegionVo getSingleVo(String districtName) {
        // hql 不能对list进行是否为空判断, list要用(:list) 格式
        String sql = "select new com.demo.database.pojo.vo.AreaCodeRegionVo(AreaCodeRegion) " +
                "from AreaCodeRegion  " +
                "where isDeleted = false " +
                "  and districtName like :districtName ";

        // 根据 sql 语句构建 query
        TypedQuery<AreaCodeRegionVo> query = entityManager.createQuery(sql, AreaCodeRegionVo.class);
        // 填充变量
        query.setParameter("districtName", districtName);
        // getSingleResult 查询不到结果时会报错
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
