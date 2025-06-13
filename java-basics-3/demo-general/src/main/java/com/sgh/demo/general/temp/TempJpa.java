package com.sgh.demo.general.temp;

import com.sgh.demo.common.database.db.entity.DemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Song gh
 * @version 2025/5/26
 */
@Repository
public interface TempJpa extends JpaRepository<StudentFiles, Long> {

    @Query(value = "SELECT sc.city, sc.county, stu.*\n" +
            "FROM zkxy_student_files AS stu\n" +
            "         INNER JOIN zkxy_school AS sc ON sc.deleted = FALSE AND sc.uid = stu.school_uid\n" +
            "WHERE stu.deleted = FALSE\n" +
            "  AND sc.city = '杭州市'\n" +
            "  AND (sc.county = '江干区' OR sc.county = '上城区')\n" +
            "ORDER BY school_uid;", nativeQuery = true)
    List<StudentFiles> getCert();
}
