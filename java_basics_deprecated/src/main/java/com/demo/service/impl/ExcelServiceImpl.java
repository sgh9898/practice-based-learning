package com.demo.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.listener.ExcelToSqlListener;
import com.demo.pojo.ExcelToSql;
import com.demo.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel Service
 * <br> 1. Excel 转 Sql 建表语句
 * <br> 2. Excel 与 Json 转换
 *
 * @author Song gh on 2022/3/2
 * @since 2022/4/2
 */
@Slf4j
@Service
public class ExcelServiceImpl implements ExcelService {

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

    /** 解析 excel, 转为 sql */
    @Override
    public String excelToSql(MultipartFile file, String tableName, String alias) throws IOException {
        // 表名, 未上传则用文件名
        String name = StringUtils.isNotBlank(tableName) ? tableName : file.getOriginalFilename();
        // 处理 excel
        ExcelToSqlListener listener = new ExcelToSqlListener(name, alias);
        EasyExcel.read(file.getInputStream(), ExcelToSql.class, listener).sheet().doRead();

        return listener.getDdlStr();
    }

    /**
     * 解析 json array, 转为 excel (所有 json object 的 key 相同)
     *
     * @see #setHeadAndData
     */
    @Override
    public void jsonToExcel(JSONArray jsonArray, String fileName, HttpServletResponse response) throws IOException {
        // 文件名校验
        String checkedFileName;
        if (StringUtils.isBlank(fileName)) {
            // 默认名
            checkedFileName = "untitled.xlsx";
        } else {
            // 检测后缀
            checkedFileName = fileName.endsWith(".xlsx") ? fileName : fileName + ".xlsx";
        }

        // 设置文件格式, 编码, 文件名
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + checkedFileName);

        // 填充 excel 列名及数据
        List<List<String>> headList = ListUtils.newArrayList();
        List<List<Object>> dataList = ListUtils.newArrayList();
        setHeadAndData(headList, dataList, jsonArray);

        EasyExcel.write(response.getOutputStream()).head(headList).sheet().doWrite(dataList);
    }

//--------------------------------------------------

    /** 根据 json 填充 excel 列名及数据 (所有 json object 的 key 相同) */
    private void setHeadAndData(List<List<String>> headList, List<List<Object>> dataList, JSONArray jsonArray) {
        // 列名, 取首个 json object 做参照
        for (String key : jsonArray.getJSONObject(0).keySet()) {
            List<String> head = ListUtils.newArrayList();
            head.add(key);
            headList.add(head);
        }

        // 逐行填充数据
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            List<Object> data = ListUtils.newArrayList();
            // 单行数据按 head 顺序填充
            for (List<String> headName : headList) {
                data.add(jsonObject.get(headName.get(0)));
            }
            dataList.add(data);
        }
    }
}
