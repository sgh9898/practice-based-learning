package com.sgh.demo.common.temp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * [数据库交互] 学校信息
 *
 * @author Song gh
 * @version 2024/04/29
 */
public interface XxxxRepository extends JpaRepository<Xxxx, String> {

    @Override
    @Query(value = "SELECT * FROM mental_health.xxxx ", nativeQuery = true)
    List<Xxxx> findAll();
}
