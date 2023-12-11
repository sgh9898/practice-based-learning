package com.demo.database.service;

import com.demo.database.db.entity.AreaCodeRegion;
import com.demo.database.pojo.query.AreaCodeRegionQueryDto;
import com.demo.database.pojo.upsert.AreaCodeRegionUpsertDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * [功能类] 区号
 *
 * @author Song gh on 2023/12/11.
 */
public interface AreaCodeRegionService {
    
    /** [新增/更新] 区号 */
    void upsert(AreaCodeRegionUpsertDto dto);
    
    /** [删除] 区号 */
    void delete(List<Integer> idList);
    
    /** [查询] 区号 */
    AreaCodeRegion get(Integer id);
    
    /** [列表] 区号 */
    List<AreaCodeRegion> getList();
    
    /** [分页] 区号 */
    Page<AreaCodeRegion> getPage(AreaCodeRegionQueryDto dto);

    /** [导入] 区号 */
    void importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response);

    /** [模板] 区号 */
    void exportExcelTemplate(HttpServletRequest request, HttpServletResponse response);

    /** [导出] 区号 */
    void exportData(HttpServletRequest request, HttpServletResponse response);
}
