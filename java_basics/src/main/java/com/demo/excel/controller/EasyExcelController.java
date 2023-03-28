package com.demo.excel.controller;

import com.demo.database.entity.DemoEntity;
import com.demo.excel.easyexcel.EasyExcelUtils;
import com.demo.excel.service.ExcelService;
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
import java.util.*;

/**
 * EasyExcel 功能测试
 *
 * @author Song gh on 2023/3/21.
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
        return EasyExcelUtils.importData(file, request, response, DemoEntity.class);
    }

    @GetMapping("/exportTemplate")
    @ApiOperation("导出模板")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) {
        EasyExcelUtils.exportTemplate(request, response, "导出模板", DemoEntity.class, "这是一条说明");
    }

    @GetMapping("/exportExcel")
    @ApiOperation("导出 Excel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        excelService.exportExcel(request, response);
    }

    @GetMapping("/noModel/exportTemplate")
    @ApiOperation("不指定 ExcelClass 导出模板, 复合 head")
    public void noModelExportData(HttpServletRequest request, HttpServletResponse response) {
        List<List<String>> outerHeadList = new LinkedList<>();
        List<String> headList1 = new ArrayList<>();
        headList1.add("一级标题1");
        headList1.add("二级标题1");
        List<String> headList2 = new ArrayList<>();
        headList2.add("一级标题1");
        headList2.add("二级标题2");
        List<String> headList3 = new ArrayList<>();
        headList3.add("二级标题3");
        outerHeadList.add(headList1);
        outerHeadList.add(headList2);
        outerHeadList.add(headList3);

        EasyExcelUtils easyExcelUtils = new EasyExcelUtils(request, response, "测试文件324");
        easyExcelUtils.setNoModelHeadList(outerHeadList);
        easyExcelUtils.setSheetName("11111");
        easyExcelUtils.noModelWriteSheet();
        easyExcelUtils.setSheetName("22222");
        easyExcelUtils.noModelWriteSheet();
        easyExcelUtils.closeExcel();
    }

    @PostMapping("/noModel/importData")
    @ApiOperation("不指定 ExcelClass 导入数据")
    public Object noModelImportData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put("二级标题1", "title1");
        headMap.put("二级标题2", "title2");
        headMap.put("二级标题3", "title3");
        return EasyExcelUtils.noModelImportExcel(file, request, response, headMap);
    }
}
