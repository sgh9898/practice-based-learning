package com.demo.database.repository;

import com.demo.database.entity.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 演示类 Repository
 *
 * @author Song gh on 2022/04/14.
 */
public interface SampleEntityRepository extends JpaRepository<SampleEntity, Long> {
}
