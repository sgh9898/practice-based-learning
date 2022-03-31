package com.demo.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Excel Service
 * <br>1. Excel 转 Sql 建表语句
 *
 * @author Song gh on 2022/3/25.
 */
public interface ExcelService {

    /** 通过浏览器下载 Excel-to-Sql 模板 */
    void downloadTemplate(HttpServletResponse response) throws IOException;

    /** 解析 Excel 并转为 Sql */
    String excelToSql(MultipartFile file, String tableName, String alias) throws IOException;
}
