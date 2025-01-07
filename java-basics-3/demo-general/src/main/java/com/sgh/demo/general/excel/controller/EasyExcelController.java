package com.sgh.demo.general.excel.controller;

import com.sgh.demo.general.excel.easyexcel.EasyExcelUtils;
import com.sgh.demo.general.excel.pojo.ExcelToDdl;
import com.sgh.demo.general.excel.service.ExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * EasyExcel 功能测试
 *
 * @author Song gh
 * @version 2024/2/7
 */
@Slf4j
@RestController
@Tag(name = "EasyExcel 功能测试")
@RequestMapping("/easyExcel")
public class EasyExcelController {

    @Resource
    private ExcelService excelService;

    @PostMapping("/noModel/importData")
    @Operation(summary = "不指定 ExcelClass 导入数据")
    public Object noModelImportData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put("二级标题1", "title1");
        headMap.put("二级标题2", "title2");
        headMap.put("二级标题3", "title3");
        return EasyExcelUtils.noModelImportExcel(file, headMap, null);
    }

    @GetMapping("/excelToDdl/template")
    @Operation(summary = "Excel 转 DDL 模板")
    public void excelToDdlTemplate(HttpServletRequest request, HttpServletResponse response) {
        EasyExcelUtils.exportTemplate(request, response, "Excel转DDL模板", ExcelToDdl.class, null);
    }

    @PostMapping("/excelToDdl")
    @Operation(summary = "Excel 转 DDL")
    public String excelToDdl(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response, @RequestParam String tableName, @RequestParam Boolean exportExcel) {
        return excelService.generateDdl(file, request, response, tableName, exportExcel);
    }
}
