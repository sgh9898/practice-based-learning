package com.demo.database.service;

import com.demo.database.db.entity.DemoEntity;
import com.demo.database.pojo.query.DemoEntityQueryDto;
import com.demo.database.pojo.upsert.DemoEntityUpsertDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * [功能类] 测试数据
 *
 * @author Song gh
 * @version 2024/03/18
 */
public interface DemoEntityService {

    /** [新增/更新] 测试数据 */
    void upsert(DemoEntityUpsertDto dto);

    /** [删除] 测试数据 */
    void delete(List<Long> idList);

    /** [查询] 测试数据 */
    DemoEntity get(Long id);

    /** [列表] 测试数据 */
    List<DemoEntity> getList();

    /** [分页] 测试数据 */
    Page<DemoEntity> getPage(DemoEntityQueryDto dto);

    /** [导入] 测试数据 */
    void importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response);

    /** [模板] 测试数据 */
    void exportExcelTemplate(HttpServletRequest request, HttpServletResponse response);

    /** [导出] 测试数据 */
    void exportData(HttpServletRequest request, HttpServletResponse response);
}
