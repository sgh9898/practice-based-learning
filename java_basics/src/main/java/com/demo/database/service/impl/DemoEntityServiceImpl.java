package com.demo.database.service.impl;

import com.demo.database.entity.DemoEntity;
import com.demo.database.pojo.DemoEntityDto;
import com.demo.database.pojo.ExcelDemoExcelVo;
import com.demo.database.repository.DemoEntityRepository;
import com.demo.database.service.DemoEntityService;
import com.demo.easyexcel.util.EasyExcelUtils;
import com.demo.easyexcel.util.ValidationUtil;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String errorMessage = ValidationUtil.validate(dto);
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

    /**
     * [Excel 新增/更新] 演示类
     *
     * @param excelList 经过基础校验的 excel 导入数据
     * @return 导入失败的信息
     */
    @Override
    public List<ExcelDemoExcelVo> checkThenSaveAll(List<ExcelDemoExcelVo> excelList) {
//        // 校验
//
//        // 查重
//        Map<> repeatedMap = new HashMap<>();
//        List<DemoEntity> repeatedList = demoEntityRepository.getRepeatedList();
//        // 整理格式
//        for (DemoEntity curr : repeatedList) {
//
//        }
//
        // 构建标准数据
        List<DemoEntity> validList = new ArrayList<>();
        List<ExcelDemoExcelVo> inValidList = new ArrayList<>();
        for (ExcelDemoExcelVo excel : excelList) {
            DemoEntity newEntity = new DemoEntity(excel);

//            // 覆盖旧数据 (如果有)
//            DemoEntity previous = ;
//            if (previous != null) {
//                newEntity.setId(previous.getId());
//                newEntity.setCreateTime(previous.getCreateTime());
//            }
            validList.add(newEntity);
        }

        // 整批数据没有报错, 存入数据库
        if (inValidList.isEmpty()) {
            demoEntityRepository.saveAll(validList);
        }
        // 返回无效数据
        return inValidList;
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
        List<DemoEntity> demoEntityList = EasyExcelUtils.getImportedEntityList(file, request, response, ExcelDemoExcelVo.class, DemoEntity.class);
    }

    /** [导出模板] 演示类 */
    @Override
    public void exportExcelTemplate(HttpServletRequest request, HttpServletResponse response) {
        // 排除的列
        Set<String> excludedCols = new HashSet<>();
        excludedCols.add("errorMessage");
        // 首行说明
        String note = "说明: 1. 黄色字段为必填项.\n" +
                "         2.";
        EasyExcelUtils.downloadTemplate(request, response, "演示类.xlsx", ExcelDemoExcelVo.class, note);
    }

    /** [导出数据] 演示类 */
    @Override
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
//        // 屏蔽列
//        Set<String> excludedCols = new HashSet<>();
//        excludedCols.add("errorMessage");
//
//        EasyExcelUtils easyExcelUtils = new EasyExcelUtils();
//
//        // 导出
//        List<ExcelDemoEntity> excelList = demoEntityRepository.getExcelList();
//        String fileName = "演示类导出" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
//        EasyExcelUtils.downloadExcluding(request, response, fileName, ExcelDemoEntity.class, excelList, excludedCols);
    }
}
