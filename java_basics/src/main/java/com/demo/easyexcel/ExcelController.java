package com.demo.easyexcel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.easyexcel.service.ExcelService;
import com.demo.util.ResultUtil;
import io.swagger.annotations.Api;
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

    /**
     * 下载 Excel-to-Sql 模板
     *
     * @param response 用于传输文件
     * @return 失败时返回含部分数据的 excel
     */
    @Operation(summary = "下载 [Excel --> Sql] 模板")
    @GetMapping("/sql/template")
    public Map<String, Object> downloadSqlTemplate(HttpServletRequest request, HttpServletResponse response) {
        log.debug("下载 [Excel --> Sql] 模板");
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
}
