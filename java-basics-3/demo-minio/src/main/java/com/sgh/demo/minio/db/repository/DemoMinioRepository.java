package com.sgh.demo.minio.db.repository;

import com.sgh.demo.minio.db.entity.DemoMinio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * [数据库交互] Minio 文件管理
 *
 * @author Song gh
 * @version 2024/04/03
 */
@Repository
public interface DemoMinioRepository extends JpaRepository<DemoMinio, Long> {

    /** 查询 Minio 文件 */
    DemoMinio findFirstByIdAndIsDeletedIsFalse(Long id);

    /** [列表] 查询 Minio 文件 */
    List<DemoMinio> findAllByIsDeletedIsFalse();

    /** [列表] 查询 Minio 文件 */
    List<DemoMinio> findAllByIsDeletedIsFalseAndIdIn(Collection<Long> idList);

    /** [分页] 查询 Minio 文件 */
    Page<DemoMinio> findAllByIsDeletedIsFalse(Pageable pageable);
}
