package com.demo.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.demo.easyexcel.annotation.ExcelDropDown;
import com.demo.easyexcel.handler.EasyExcelWriteHandler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * EasyExcel 工具类
 * <br> 1.配置 @ExcelDropDown 下拉框注解
 * <br> 2.简化 EasyExcel 导出, 配置前端下载功能
 *
 * @author Song gh on 2022/7/12.
 * @see EasyExcelWriteHandler
 */
public class EasyExcelUtil {

    /** 浏览器下载 Excel, 排除指定列 */
    public static void downloadExcel(HttpServletResponse response, String fileName, String sheetName, Class<?> targetClass, List<?> dataList) {
        downloadExcelAllArgs(response, fileName, sheetName, null, null, targetClass, dataList);
    }

    /** 浏览器下载 Excel, 排除指定列 */
    public static void downloadExcel(HttpServletResponse response, String fileName, String sheetName, Set<String> excludedCols, Class<?> targetClass, List<?> dataList) {
        downloadExcelAllArgs(response, fileName, sheetName, excludedCols, null, targetClass, dataList);
    }

    /** 浏览器下载 Excel 模板(动态下拉框) */
    public static void downloadDynamicTemplate(HttpServletResponse response, String fileName, String sheetName,
                                               Map<String, String[]> dynamicMap, Class<?> targetClass) {
        downloadExcelAllArgs(response, fileName, sheetName, null, dynamicMap, targetClass, null);
    }

    /**
     * 浏览器下载 Excel, 完整参数
     *
     * @param response     http servlet response
     * @param fileName     文件名
     * @param sheetName    表名
     * @param excludedCols 排除的列
     * @param dynamicMap   动态下拉框内容, Map of <ExcelDropDown.name, 下拉框内容>
     * @param targetClass  待转换的实体类
     * @param dataList     表内数据, 不填充传 null 即可
     */
    public static void downloadExcelAllArgs(HttpServletResponse response, String fileName, String sheetName, Set<String> excludedCols,
                                            Map<String, String[]> dynamicMap, Class<?> targetClass, List<?> dataList) {
        // 设置 header
        setHeader(response, fileName);

        // 根据下拉框注解, 填充相关内容
        Map<Integer, String[]> columnMap;
        // 静态下拉框 dynamicMap == null
        columnMap = generateColumnMap(targetClass, dynamicMap);

        // 导出
        exportExcel(response, sheetName, excludedCols, targetClass, dataList, columnMap);
    }

    //------------------------------------------------------------

    /** 配置 header */
    private static void setHeader(HttpServletResponse response, String fileName) {
        // 设置文件格式, 编码
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");

        // 设置文件名
        if (fileName == null || StringUtils.isEmpty(fileName)) {
            fileName = "untitled.xlsx";
        } else if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            fileName = fileName + ".xlsx";
        }
        fileName = new String(fileName.getBytes(), StandardCharsets.ISO_8859_1);
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
    }

    /** 导出为 Excel */
    private static void exportExcel(HttpServletResponse response, String sheetName, Set<String> excludedCols,
                                    Class<?> targetClass, List<?> dataList, Map<Integer, String[]> columnMap) {
        // output stream 会自动关闭
        ExcelWriter excelWriter;

        // 导出
        try {
            excelWriter = EasyExcel.write(response.getOutputStream(), targetClass)
                    .excludeColumnFieldNames(excludedCols)
                    // 下拉框
                    .registerWriteHandler(new EasyExcelWriteHandler(columnMap))
                    // 自动列宽
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).build();
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            if (dataList == null || dataList.isEmpty()) {
                dataList = new ArrayList<>();
            }
            excelWriter.write(dataList, writeSheet);
        } catch (IOException e) {
            throw new RuntimeException("Excel 导出失败");
        }
        excelWriter.finish();
    }

    /**
     * 处理 @ExcelDropDown 注解
     *
     * @param targetClass 待转换的实体类
     * @param dynamicMap  动态下拉框内容, Map of <ExcelDropDown.name, 下拉框内容>
     * @return Map of <index, 下拉框内容>
     */
    private static Map<Integer, String[]> generateColumnMap(Class<?> targetClass, Map<String, String[]> dynamicMap) {
        // 获取 Class 所有字段
        Field[] fieldArray = targetClass.getDeclaredFields();
        Map<Integer, String[]> dropDownMap = new HashMap<>();
        Field field;

        // 查找有下拉框注解的字段, 填充相关数据
        for (int index = 0; index < fieldArray.length; index++) {
            field = fieldArray[index];
            ExcelDropDown excelDropDown = field.getAnnotation(ExcelDropDown.class);

            // 检测到 @ExcelDropDown 注解
            if (excelDropDown != null) {
                String name = excelDropDown.name();
                if (!StringUtils.isEmpty(name) && dynamicMap != null) {
                    // @ExcelDropDown.name 与 dynamicMap.key 相同, 填充内容
                    String[] dynamicOptions = dynamicMap.get(name);
                    if (dynamicOptions != null && dynamicOptions.length > 0) {
                        buildDropDownMap(dropDownMap, dynamicOptions, excelDropDown, index);
                    } else {
                        buildDropDownMap(dropDownMap, null, excelDropDown, index);
                    }
                } else {
                    buildDropDownMap(dropDownMap, null, excelDropDown, index);
                }
            }
        }
        return dropDownMap;
    }

    /** 填充下拉框内容 */
    private static void buildDropDownMap(Map<Integer, String[]> dropDownMap, String[] dynamicOptions,
                                         ExcelDropDown excelDropDown, int index) {
        // 注解为空, 无后续步骤
        if (!Optional.ofNullable(excelDropDown).isPresent()) {
            return;
        }
        if (dynamicOptions != null && dynamicOptions.length > 0) {
            dropDownMap.put(index, dynamicOptions);
        } else {
            String[] source = excelDropDown.options();
            if (source != null && source.length > 0) {
                dropDownMap.put(index, source);
            }
        }
    }
}

