package com.demo.database.service.impl;

import com.demo.database.db.entity.Region;
import com.demo.database.db.repository.RegionRepository;
import com.demo.database.pojo.excel.ExcelRegion;
import com.demo.database.pojo.query.RegionQueryDto;
import com.demo.database.pojo.upsert.RegionUpsertDto;
import com.demo.database.service.RegionService;
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
 * @author Song gh on 2023/12/15.
 */
@Service
public class RegionServiceImpl implements RegionService {

    @Resource
    private RegionRepository regionRepository;

    /** [新增/更新] 区号 */
    @Override
    public void upsert(RegionUpsertDto dto) {
        // 仅当 id 存在且有效时更新数据, 否则新增数据
        Integer id = dto.getId();
        if (id != null) {
            Region optData = regionRepository.findFirstByIdAndIsDeletedIsFalse(id);
            if (optData != null) {
                // 数据有效, 更新并记录时间
                optData.update(dto);
                regionRepository.save(optData);
                return;
            }
        }
        // id 不存在或无效则新增
        regionRepository.save(new Region(dto));
    }

    /** [删除] 区号 */
    @Override
    public void delete(List<Integer> idList) {
        regionRepository.markDeletedByIdList(idList);
    }

    /** [查询] 区号 */
    @Override
    public Region get(Integer id) {
        return regionRepository.findFirstByIdAndIsDeletedIsFalse(id);
    }

    /** [列表] 区号 */
    @Override
    public List<Region> getList() {
        return regionRepository.findAllByIsDeletedIsFalse();
    }

    /** [分页] 区号 */
    @Override
    public Page<Region> getPage(RegionQueryDto dto) {
        // 分页参数
        int page = Math.max(0, dto.getPage() - 1);
        int size = dto.getSize();
        if (size <= 0) {
            size = 10;
        }
        return regionRepository.findAllByIsDeletedIsFalse(PageRequest.of(page, size));
    }

    /** [导入] 区号 */
    @Override
    public void importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        List<ExcelRegion> excelList = EasyExcelUtils.importExcel(file, request, response, ExcelRegion.class);
        List<Region> entityList = new LinkedList<>();
        if (excelList != null && !excelList.isEmpty()) {
            excelList.forEach(currExcel -> entityList.add(new Region(currExcel)));
        }
        regionRepository.saveAll(entityList);
    }

    /** [模板] 区号 */
    @Override
    public void exportExcelTemplate(HttpServletRequest request, HttpServletResponse response) {
        // 首行说明
        String note = "说明: 1. 黄色字段为必填项.\n" +
                "         2. 时间格式为 yyyy-MM-dd HH:mm:ss\n";
        EasyExcelUtils.exportTemplate(request, response, "区号模板.xlsx", ExcelRegion.class, note);
    }

    /** [导出] 区号 */
    @Override
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        List<ExcelRegion> excelList = regionRepository.getExcelList();
        EasyExcelUtils.exportData(request, response, "区号导出数据", ExcelRegion.class, excelList);
    }
}
