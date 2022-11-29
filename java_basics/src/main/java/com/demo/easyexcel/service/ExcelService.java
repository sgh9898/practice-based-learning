package com.demo.easyexcel.service;

import com.alibaba.fastjson.JSONArray;
import com.demo.sample.entity.DemoEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * Excel Service
 * <br> 1. Excel 转 Sql 建表语句
 * <br> 2. Excel 与 Json 转换
 *
 * @author Song gh on 2022/3/2
 * @since 2022/4/2
 */
public interface ExcelService {

    /** 通过浏览器下载 Excel-to-Sql 模板 */
    void downloadTemplate(HttpServletRequest request, HttpServletResponse response);

    /** 解析 excel, 转为 sql */
    String excelToSql(MultipartFile file, String tableName, String alias) throws IOException;

    /** 解析 json array, 转为 excel (所有 json object 的 key 相同) */
    void jsonToExcel(JSONArray jsonArray, String fileName, HttpServletResponse response) throws IOException;

    void test(@Valid DemoEntity demoEntity);
}
