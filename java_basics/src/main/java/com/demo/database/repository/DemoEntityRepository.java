package com.demo.database.repository;

import com.demo.database.entity.DemoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 演示类 Repository
 *
 * @author Song gh on 2022/04/14.
 */
public interface DemoEntityRepository extends JpaRepository<DemoEntity, Long> {

    List<DemoEntity> findAllByName(String name, Pageable pageable);
}
