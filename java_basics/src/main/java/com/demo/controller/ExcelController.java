package com.demo.controller;

import com.demo.service.ExcelService;
import com.demo.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Excel Controller
 *
 * @author Song gh on 2022/3/28.
 */
@Api(tags = "Excel Controller")
@RestController
@RequestMapping("/excel")
public class ExcelController {
    private static final Logger log = LoggerFactory.getLogger(ExcelController.class);

    // Services
    @Resource
    private ExcelService excelService;

    /**
     * 通过浏览器下载 Excel-to-Sql 模板
     *
     * @param response 用于传输文件
     * @return 失败时返回含部分数据的 excel
     */
    @ApiOperation("下载 Excel-to-Sql 模板")
    @PostMapping("/sql/template")
    public Map<String, Object> downloadTemplate(HttpServletResponse response) throws IOException {
        log.debug("下载 Excel-to-Sql 模板");
        excelService.downloadTemplate(response);
        return ResultUtil.success();
    }

    /**
     * excel 转 sql 建表语句
     *
     * @param file 上传的 excel
     * @return sql 建表语句
     */
    @ApiOperation("Excel-to-Sql 转换")
    @PostMapping("/sql/convert")
    @ApiImplicitParams({@ApiImplicitParam(name = "tableName", value = "数据库表名"),
                    @ApiImplicitParam(name = "alias", value = "表别名")})
    // @RequestPart("file") 解决 swagger 测试下接收不到文件的问题
    public Map<String, Object> upload(@RequestPart("file") MultipartFile file, String tableName, String alias) throws IOException {
        if (file == null) {
            return ResultUtil.error("文件为空");
        }
        return ResultUtil.success(excelService.excelToSql(file, tableName, alias));
    }
}
