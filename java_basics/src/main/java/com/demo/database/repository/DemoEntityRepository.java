package com.demo.database.repository;

import com.demo.database.entity.DemoEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 演示类 Repository
 *
 * @author Song gh on 2022/04/14.
 */
public interface DemoEntityRepository extends JpaRepository<DemoEntity, Long> {

    Optional<DemoEntity> findFirstByNameAndIsDeletedIsFalse(String name);

    @Query(nativeQuery = true, value = "select * from demo_entity where id in :idList")
    List<DemoEntity> queryByList(List<Long> idList);

    @Query(nativeQuery = true, value = "select * from demo_entity where name = :name")
    Page<Map<String, Object>> findAllByName(@Param("name") String name, Pageable pageable);
}
