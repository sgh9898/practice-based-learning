package com.demo.db.repository;

import com.demo.db.pojo.DemoEntityVo;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * 自定义 Repository 方法
 *
 * @author Song gh on 2022/7/18.
 */
@Service
public class DemoEntityRepositoryImpl implements DemoEntityCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DemoEntityVo getSingleVo(String name) {
        // hql 不能对list进行是否为空判断, list要用(:list) 格式
        String sql = "select new com.demo.db.pojo.DemoEntityVo(demo) from DemoEntity as demo " +
                "where demo.name = :name " +
                "   and date(demo.updateTime) = current_date ";

        // 根据 sql 语句构建 query
        TypedQuery<DemoEntityVo> query = entityManager.createQuery(sql, DemoEntityVo.class);
        // 填充变量
        query.setParameter("name", name);
        // getSingleResult 查询不到结果时会报错
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
