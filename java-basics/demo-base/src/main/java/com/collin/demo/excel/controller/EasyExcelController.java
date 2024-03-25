package com.collin.demo.excel.controller;

import com.collin.demo.excel.easyexcel.EasyExcelUtils;
import com.collin.demo.excel.pojo.ExcelToDdl;
import com.collin.demo.excel.service.ExcelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@Api(tags = "EasyExcel 功能测试")
@RequestMapping("/easyExcel")
public class EasyExcelController {

    @Resource
    private ExcelService excelService;

    @PostMapping("/importData")
    @ApiOperation("导入数据")
    public Object importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        return excelService.importData(file, request, response);
    }

    @GetMapping("/exportTemplate")
    @ApiOperation("导出模板")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) {
        excelService.exportTemplate(request, response);
    }

    @GetMapping("/exportData")
    @ApiOperation("导出数据")
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        excelService.exportData(request, response);
    }

    @GetMapping("/exportExcel")
    @ApiOperation("导出自定义数据")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        excelService.exportExcel(request, response);
    }

    @GetMapping("/noModel/export")
    @ApiOperation("不指定 ExcelClass 导出")
    public void noModelExport(HttpServletRequest request, HttpServletResponse response) {
        excelService.noModelExportExcel(request, response);
    }

    @PostMapping("/noModel/importData")
    @ApiOperation("不指定 ExcelClass 导入数据")
    public Object noModelImportData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put("二级标题1", "title1");
        headMap.put("二级标题2", "title2");
        headMap.put("二级标题3", "title3");
        return EasyExcelUtils.noModelImportExcel(file, headMap);
    }

    @GetMapping("/excelToDdl/template")
    @ApiOperation("Excel 转 DDL 模板")
    public void excelToDdlTemplate(HttpServletRequest request, HttpServletResponse response) {
        EasyExcelUtils.exportTemplate(request, response, "Excel转DDL模板", ExcelToDdl.class, null);
    }

    @PostMapping("/excelToDdl")
    @ApiOperation("Excel 转 DDL ")
    public String excelToDdl(MultipartFile file, HttpServletRequest request, HttpServletResponse response, String tableName) {
        return excelService.generateDdl(file, request, response, tableName);
    }
}
