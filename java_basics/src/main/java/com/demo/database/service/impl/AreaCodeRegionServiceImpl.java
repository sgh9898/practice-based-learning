package com.demo.database.service.impl;

import com.demo.database.db.entity.AreaCodeRegion;
import com.demo.database.db.repository.AreaCodeRegionRepository;
import com.demo.database.pojo.excel.ExcelAreaCodeRegion;
import com.demo.database.pojo.query.AreaCodeRegionQueryDto;
import com.demo.database.pojo.upsert.AreaCodeRegionUpsertDto;
import com.demo.database.service.AreaCodeRegionService;
import com.demo.excel.easyexcel.EasyExcelUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

/**
 * [功能类] 区号
 *
 * @author Song gh on 2023/12/11.
 */
@Service
public class AreaCodeRegionServiceImpl implements AreaCodeRegionService {
    
    @Resource
    private AreaCodeRegionRepository areaCodeRegionRepository;
    
    /** [新增/更新] 区号 */
    @Override
    public void upsert(AreaCodeRegionUpsertDto dto) {
        // 仅当 id 存在且有效时更新数据, 否则新增数据
        Integer id = dto.getId();
        if (id != null) {
            AreaCodeRegion optData = areaCodeRegionRepository.findFirstByIdAndIsDeletedIsFalse(id);
            if (optData != null) {
                // 数据有效, 更新并记录时间
                optData.update(dto);
                areaCodeRegionRepository.save(optData);
                return;
            }
        }
        // id 不存在或无效则新增
        areaCodeRegionRepository.save(new AreaCodeRegion(dto));
    }
    
    /** [删除] 区号 */
    @Override
    public void delete(List<Integer> idList) {
        areaCodeRegionRepository.markDeletedByIdList(idList);
    }
    
    /** [查询] 区号 */
    @Override
    public AreaCodeRegion get(Integer id){
        return areaCodeRegionRepository.findFirstByIdAndIsDeletedIsFalse(id);
    }
    
    /** [列表] 区号 */
    @Override
    public List<AreaCodeRegion> getList(){
        return areaCodeRegionRepository.findAllByIsDeletedIsFalse();
    }
    
    /** [分页] 区号 */
    @Override
    public Page<AreaCodeRegion> getPage(AreaCodeRegionQueryDto dto){
        // 分页参数
        int page = Math.max(0, dto.getPage() - 1);
        int size = dto.getSize();
        if (size <= 0) {
            size = 10;
        }
        return areaCodeRegionRepository.findAllByIsDeletedIsFalse(PageRequest.of(page, size));
    }

    /** [导入] 区号 */
    @Override
    public void importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        List<ExcelAreaCodeRegion> excelList = EasyExcelUtils.importData(file, request, response, ExcelAreaCodeRegion.class);
        List<AreaCodeRegion> entityList = new LinkedList<>();
        if (excelList != null && !excelList.isEmpty()) {
            excelList.forEach(currExcel -> entityList.add(new AreaCodeRegion(currExcel)));
        }
        areaCodeRegionRepository.saveAll(entityList);
    }

    /** [模板] 区号 */
    @Override
    public void exportExcelTemplate(HttpServletRequest request, HttpServletResponse response) {
        // 首行说明
        String note = "说明: 1. 黄色字段为必填项.\n" +
                "         2. 时间格式为 yyyy-MM-dd HH:mm:ss\n";
        EasyExcelUtils.exportTemplate(request, response, "区号模板.xlsx", ExcelAreaCodeRegion.class, note);
    }
    
    /** [导出] 区号 */
    @Override
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        List<ExcelAreaCodeRegion> excelList = areaCodeRegionRepository.getExcelList();
        EasyExcelUtils.exportData(request, response, "区号导出数据", ExcelAreaCodeRegion.class, excelList);
    }
}
