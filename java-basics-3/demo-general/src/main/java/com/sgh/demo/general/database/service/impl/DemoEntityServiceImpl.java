package com.sgh.demo.common.database.service.impl;

import com.sgh.demo.common.database.db.entity.DemoEntity;
import com.sgh.demo.common.database.db.repository.DemoEntityRepository;
import com.sgh.demo.common.database.pojo.excel.ExcelDemoEntity;
import com.sgh.demo.common.database.pojo.query.DemoEntityQueryDto;
import com.sgh.demo.common.database.pojo.upsert.DemoEntityUpsertDto;
import com.sgh.demo.common.database.service.DemoEntityService;
import com.sgh.demo.common.excel.easyexcel.EasyExcelUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

/**
 * [功能类] 测试数据
 *
 * @author Song gh
 * @version 2024/03/18
 */
@Service
public class DemoEntityServiceImpl implements DemoEntityService {

    @Resource
    private DemoEntityRepository demoEntityRepository;

    /** [新增/更新] 测试数据 */
    @Override
    public void upsert(DemoEntityUpsertDto dto) {
        // 仅当 id 存在且有效时更新数据, 否则新增数据
        Long id = dto.getId();
        if (id != null) {
            DemoEntity optData = demoEntityRepository.findFirstByIdAndIsDeletedIsFalse(id);
            if (optData != null) {
                // 数据有效, 更新并记录时间
                optData.update(dto);
                demoEntityRepository.save(optData);
                return;
            }
        }
        // id 不存在或无效则新增
        demoEntityRepository.save(new DemoEntity(dto));
    }

    /** [删除] 测试数据 */
    @Override
    public void delete(List<Long> idList) {
        demoEntityRepository.markDeletedByIdList(idList);
    }

    /** [查询] 测试数据 */
    @Override
    public DemoEntity get(Long id) {
        return demoEntityRepository.findFirstByIdAndIsDeletedIsFalse(id);
    }

    /** [列表] 测试数据 */
    @Override
    public List<DemoEntity> getList() {
        return demoEntityRepository.findAllByIsDeletedIsFalse();
    }

    /** [分页] 测试数据 */
    @Override
    public Page<DemoEntity> getPage(DemoEntityQueryDto dto) {
        return demoEntityRepository.findAllByIsDeletedIsFalse(PageRequest.of(dto.getPage() - 1, dto.getSize()));
    }

    /** [导入] 测试数据 */
    @Override
    public void importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        List<ExcelDemoEntity> excelList = EasyExcelUtils.importExcel(file, request, response, ExcelDemoEntity.class);
        List<DemoEntity> entityList = new LinkedList<>();
        if (excelList != null && !excelList.isEmpty()) {
            excelList.forEach(currExcel -> entityList.add(new DemoEntity(currExcel)));
        }
        demoEntityRepository.saveAll(entityList);
    }

    /** [模板] 测试数据 */
    @Override
    public void exportExcelTemplate(HttpServletRequest request, HttpServletResponse response) {
        // 首行说明
        String note = "说明: 1. 黄色字段为必填项.\n" +
                "         2. 时间格式为 yyyy-MM-dd HH:mm:ss\n";
        EasyExcelUtils.exportTemplate(request, response, "测试数据模板.xlsx", ExcelDemoEntity.class, note);
    }

    /** [导出] 测试数据 */
    @Override
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        List<ExcelDemoEntity> excelList = demoEntityRepository.getExcelList();
        EasyExcelUtils.exportData(request, response, "测试数据导出数据", ExcelDemoEntity.class, excelList);
    }
}
