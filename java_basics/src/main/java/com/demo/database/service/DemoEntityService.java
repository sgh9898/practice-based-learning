package com.demo.database.service;

import com.demo.database.entity.DemoEntity;
import com.demo.database.pojo.DemoEntityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 演示类
 *
 * @author Song gh on 2023/02/28.
 */
public interface DemoEntityService {

    /** [新增/更新] 演示类 */
    void upsert(DemoEntityDto dto);

    /** [逻辑删除] 演示类 */
    void delete(List<Long> idList);

    /** [单查询] 演示类 */
    DemoEntity get(Long id);

    /** [列表查询] 演示类 */
    List<DemoEntity> getList();

    /** [分页查询] 演示类 */
    Page<DemoEntity> getPage(Pageable pageable);

    /** [导入数据] 演示类 */
    void importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response);

    /** [导出模板] 演示类 */
    void exportExcelTemplate(HttpServletRequest request, HttpServletResponse response);

    /** [导出数据] 演示类 */
    void exportData(HttpServletRequest request, HttpServletResponse response);
}
