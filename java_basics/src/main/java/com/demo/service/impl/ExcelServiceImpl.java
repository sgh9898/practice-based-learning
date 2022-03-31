package com.demo.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.FileUtils;
import com.demo.listener.ExcelToSqlListener;
import com.demo.pojo.ExcelToSql;
import com.demo.service.ExcelService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Excel Service
 * <br>1. Excel 转 Sql 建表语句
 *
 * @author Song gh on 2022/3/25.
 */
@Service
public class ExcelServiceImpl implements ExcelService {
    private static final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);

    /** 通过浏览器下载 Excel-to-Sql 模板 */
    @Override
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 设置文件格式, 编码, 文件名
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=Excel-to-Sql Template.xlsx");

        // output stream 会自动关闭
        EasyExcel.write(response.getOutputStream(), ExcelToSql.class).sheet().doWrite(new ArrayList<>());
    }

    /** 解析 Excel 并转为 Sql */
    @Override
    public String excelToSql(MultipartFile file, String tableName, String alias) throws IOException {
        // 表名, 未上传则用文件名
        String name = StringUtils.isNotBlank(tableName) ? tableName : file.getOriginalFilename();
        // 处理 excel
        ExcelToSqlListener listener = new ExcelToSqlListener(name, alias);
        EasyExcel.read(file.getInputStream(), ExcelToSql.class, listener).sheet().doRead();

        return listener.getDdlStr();
    }
}
