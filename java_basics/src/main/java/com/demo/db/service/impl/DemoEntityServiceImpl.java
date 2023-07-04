package com.demo.db.service.impl;

import com.demo.db.entity.DemoEntity;
import com.demo.db.pojo.DemoEntityDto;
import com.demo.db.repository.DemoEntityRepository;
import com.demo.db.service.DemoEntityService;
import com.demo.excel.easyexcel.EasyExcelUtils;
import com.demo.util.ValidationUtils;
import com.demo.exception.BaseException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 演示类
 *
 * @author Song gh on 2023/02/28.
 */
@Service
public class DemoEntityServiceImpl implements DemoEntityService {

    @Resource
    private DemoEntityRepository demoEntityRepository;

    /** [新增/更新] 演示类 */
    @Override
    public void upsert(DemoEntityDto dto) {
        // 基础校验
        String errorMessage = ValidationUtils.validate(dto);
        if (StringUtils.isNotBlank(errorMessage)) {
            throw new BaseException(errorMessage);
        }

        // 仅当 id 存在且有效时更新数据, 否则新增数据
        Long id = dto.getId();
        if (id != null) {
            DemoEntity optData = demoEntityRepository.findFirstByIdAndIsDeletedIsFalse(id);
            // 数据存在
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

    /** [逻辑删除] 演示类 */
    @Override
    @Transactional
    public void delete(List<Long> idList) {
        demoEntityRepository.markDeletedByIdList(idList);
    }

    /** [单查询] 演示类 */
    @Override
    public DemoEntity get(Long id) {
        return demoEntityRepository.findFirstByIdAndIsDeletedIsFalse(id);
    }

    /** [列表查询] 演示类 */
    @Override
    public List<DemoEntity> getList() {
        return demoEntityRepository.findAllByIsDeletedIsFalse();
    }

    /** [分页查询] 演示类 */
    @Override
    public Page<DemoEntity> getPage(Pageable pageable) {
        return demoEntityRepository.findAllByIsDeletedIsFalse(pageable);
    }

    /** [导入数据] 演示类 */
    @Override
    public void importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        List<DemoEntity> importList = EasyExcelUtils.importData(file, request, response, DemoEntity.class);
        if (importList != null && !importList.isEmpty()) {
            demoEntityRepository.saveAll(importList);
        }
    }

    /** [导出模板] 演示类 */
    @Override
    public void exportExcelTemplate(HttpServletRequest request, HttpServletResponse response) {
        // 首行说明
        String note = "说明: 1. 黄色字段为必填项.\n" +
                "         2.";
        EasyExcelUtils.exportTemplate(request, response, "演示类.xlsx", DemoEntity.class, note);
    }

    /** [导出数据] 演示类 */
    @Override
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        List<DemoEntity> entityList = demoEntityRepository.findAllByIsDeletedIsFalse();
        EasyExcelUtils.exportData(request, response, "导出数据", DemoEntity.class, entityList);
    }
}
