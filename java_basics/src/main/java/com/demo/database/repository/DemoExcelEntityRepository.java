package com.demo.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 演示类 Repository
 *
 * @author Song gh on 2022/04/14.
 */
@Repository
public interface DemoExcelEntityRepository extends JpaRepository<DemoExcelEntity, Long> {

    /** 列表查询 */
    List<DemoExcelEntity> findAllByIsDeletedIsFalse();
}

