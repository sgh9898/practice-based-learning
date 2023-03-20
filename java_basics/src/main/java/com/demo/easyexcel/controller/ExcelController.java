package com.demo.easyexcel.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.database.entity.DemoEntity;
import com.demo.database.repository.DemoExcelEntityRepository;
import com.demo.easyexcel.pojo.DemoExcelVo;
import com.demo.easyexcel.service.ExcelService;
import com.demo.easyexcel.util.EasyExcelUtils;
import com.demo.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Excel
 *
 * @author Song gh on 2022/3/28.
 */
@Slf4j
@RestController
@Api(tags = "Excel 功能")
@RequestMapping("/excel")
public class ExcelController {

    // Services
    @Resource
    private ExcelService excelService;

    @Resource
    private DemoExcelEntityRepository demoExcelEntityRepository;

    /**
     * 导出 Excel-to-Sql 模板
     *
     * @param response 用于传输文件
     * @return 失败时返回含部分数据的 excel
     */
    @Operation(summary = "导出 [Excel --> Sql] 模板")
    @GetMapping("/sql/template")
    public Map<String, Object> downloadSqlTemplate(HttpServletRequest request, HttpServletResponse response) {
        log.debug("导出 [Excel --> Sql] 模板");
        excelService.downloadTemplate(request, response);
        // response 下载文件后已自动关闭, 必须 return null
        return null;
    }

    /**
     * excel 转 sql 建表语句
     *
     * @param file 上传的 excel
     * @return sql 建表语句
     */
    @Operation(summary = "Excel --> Sql")
    @PostMapping("/sql/toSql")
    @Parameter(name = "tableName", description = "数据库表名")
    @Parameter(name = "alias", description = "数据库表别名")
    public Map<String, Object> excelToSql(MultipartFile file, String tableName, String alias) throws IOException {
        // 校验
        if (file == null) {
            return ResultUtil.error("文件为空");
        }

        log.debug("[Excel --> Sql]: {}", file.getOriginalFilename());
        return ResultUtil.success(excelService.excelToSql(file, tableName, alias));
    }

    @Operation(summary = "Json Array --> Excel (Json Object Key 一致)")
    @PostMapping("/json/toExcel")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject("{\"fileName\":\"\", \"data\":[]}")}))
    public Map<String, Object> jsonToExcel(@RequestBody JSONObject jsonObject,
                                           HttpServletResponse response) throws IOException {
        // 校验
        JSONArray array = jsonObject.getJSONArray("data");
        if (array == null || array.isEmpty()) {
            return ResultUtil.error("数据为空");
        }

        log.debug("[Json Array --> Excel], {}", jsonObject);
        excelService.jsonToExcel(array, jsonObject.getString("fileName"), response);
        return null;
    }

    @PostMapping("/import/excel")
    @ApiOperation("导入数据, Excel 格式")
    public Map<String, Object> importAsExcel(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        List<DemoExcelEntity> excelVoList = EasyExcelUtils.importAsExcel(file, request, response, DemoExcelEntity.class);
        if (excelVoList == null) {
            return null;
        }
        return ResultUtil.success("data", excelVoList);
    }

    @PostMapping("/import/entity")
    @ApiOperation("导入数据, Entity 格式")
    public Map<String, Object> importAsEntity(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        List<DemoEntity> entityList = EasyExcelUtils.importAsEntity(file, request, response, DemoExcelVo.class, DemoEntity.class);
        if (entityList == null) {
            return null;
        }
        return ResultUtil.success("data", entityList);
    }

    @GetMapping("/exportTemplate")
    @ApiOperation("导出模板")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) {
        EasyExcelUtils.exportTemplate(request, response, "测试excel.xlsx", DemoExcelVo.class, "测试表格说明");
    }

    @GetMapping("/exportData")
    @ApiOperation("导出数据")
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        excelService.exportData(request, response);
    }

    @GetMapping("/listData")
    @ApiOperation("导出数据")
    public List<DemoExcelEntity> listData(HttpServletRequest request, HttpServletResponse response) {
        return demoExcelEntityRepository.findAllByIsDeletedIsFalse();
    }

    @GetMapping("/exportData/noModel")
    @ApiOperation("导出数据, 不指定 Excel 类")
    public void exportDataNoModel(HttpServletRequest request, HttpServletResponse response) {
        excelService.exportDataNoModel(request, response);
    }
}
