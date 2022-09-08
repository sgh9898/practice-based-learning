package com.demo.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.demo.easyexcel.annotation.ExcelDropDown;
import com.demo.easyexcel.handler.EasyExcelWriteHandler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
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

    /** 浏览器下载 Excel */
    public static void downloadExcel(HttpServletRequest request, HttpServletResponse response,
                                     String fileName, String sheetName, Class<?> targetClass, List<?> dataList) {
        downloadExcelAllArgs(request, response, fileName, sheetName, null, null, null, targetClass, dataList);
    }

    /** 浏览器下载 Excel, 排除指定列 */
    public static void downloadExcel(HttpServletRequest request, HttpServletResponse response,
                                     String fileName, String sheetName, Set<String> excludedCols, Class<?> targetClass, List<?> dataList) {
        downloadExcelAllArgs(request, response, fileName, sheetName, excludedCols, null, null, targetClass, dataList);
    }

    /** 浏览器下载 Excel 模板 */
    public static void downloadTemplate(HttpServletRequest request, HttpServletResponse response, String fileName, Class<?> targetClass) {
        downloadExcelAllArgs(request, response, fileName, null, null, null, null, targetClass, null);
    }

    /** 浏览器下载 Excel 模板, 排除指定列 */
    public static void downloadTemplate(HttpServletRequest request, HttpServletResponse response, String fileName, Class<?> targetClass, Set<String> excludedCols) {
        downloadExcelAllArgs(request, response, fileName, null, excludedCols, null, null, targetClass, null);
    }

    /** 浏览器下载 Excel 模板, 合并首行单元格作为说明 */
    public static void downloadTemplate(HttpServletRequest request, HttpServletResponse response, String fileName, Class<?> targetClass, String note) {
        downloadExcelAllArgs(request, response, fileName, null, null, note, null, targetClass, null);
    }

    /** 浏览器下载 Excel 模板, 排除指定列, 合并首行单元格作为说明 */
    public static void downloadTemplate(HttpServletRequest request, HttpServletResponse response, String fileName, Class<?> targetClass, Set<String> excludedCols, String note) {
        downloadExcelAllArgs(request, response, fileName, null, excludedCols, note, null, targetClass, null);
    }

    /**
     * 浏览器下载 Excel, 完整参数
     *
     * @param request      http servlet request
     * @param response     http servlet response
     * @param fileName     文件名
     * @param sheetName    表名
     * @param excludedCols 排除的列
     * @param note         首行说明
     * @param dynamicMap   动态下拉框内容, Map of <ExcelDropDown.name, 下拉框内容>
     * @param targetClass  待转换的实体类
     * @param dataList     表内数据, 不填充传 null 即可
     */
    public static void downloadExcelAllArgs(HttpServletRequest request, HttpServletResponse response, String fileName, String sheetName, Set<String> excludedCols,
                                            String note, Map<String, String[]> dynamicMap, Class<?> targetClass, List<?> dataList) {
        // 设置 header
        setHeader(request, response, fileName);

        // 根据下拉框注解, 填充相关内容, 需要考虑屏蔽的列
        Map<Integer, String[]> columnMap = generateColumnMap(targetClass, dynamicMap, excludedCols);

        // 导出
        exportExcel(response, sheetName, targetClass, dataList, excludedCols, note, columnMap);
    }

    //------------------------------------------------------------

    /** 配置 header */
    private static void setHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
        // 设置文件格式, 编码
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");

        // 设置文件名
        if (fileName == null || StringUtils.isEmpty(fileName)) {
            fileName = "untitled.xlsx";
        } else if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            fileName = fileName + ".xlsx";
        }

        // 处理中文乱码
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.contains("MSIE") || userAgent.contains("Trident") || userAgent.contains("Chrome")) {
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            fileName = new String(fileName.getBytes(), StandardCharsets.ISO_8859_1);
        }
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
    }

    /**
     * 导出为 Excel
     *
     * @param response     http servlet response
     * @param sheetName    表名
     * @param targetClass  待转换的实体类
     * @param dataList     表内数据, 不填充传 null 即可
     * @param excludedCols 排除的列
     * @param note         首行说明
     * @param columnMap    动态下拉框
     */
    private static void exportExcel(HttpServletResponse response, String sheetName, Class<?> targetClass,
                                    List<?> dataList, Set<String> excludedCols, String note, Map<Integer, String[]> columnMap) {
        // 防报错
        if (excludedCols == null) {
            excludedCols = new HashSet<>();
        }
        // output stream 会自动关闭
        ExcelWriter excelWriter;

        // 计算最后一行 index, 同时统计列宽
        Integer lastColIndex = null;
        List<Integer> columnSizeList = new ArrayList<>();
        if (StringUtils.isNotEmpty(note)) {
            // 记录列宽
            Field[] fieldArray = targetClass.getDeclaredFields();
            for (Field field : fieldArray) {
                ExcelProperty excelAnnotation = field.getAnnotation(ExcelProperty.class);
                if (excelAnnotation != null) {
                    columnSizeList.add((excelAnnotation.value())[0].length());
                }
            }
            // 获取最后一列的 index
            lastColIndex = fieldArray.length;
            // 除去排除的列
            lastColIndex -= excludedCols.size();
            lastColIndex = Math.max(lastColIndex, 0);
        }

        // 导出
        try {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            // 存在自定义说明
            if (StringUtils.isNotBlank(note)) {
                // 说明内容
                List<List<Object>> tempList = new ArrayList<>();
                List<Object> temp = new ArrayList<>();
                temp.add(note);
                tempList.add(temp);
                // 导出设置
                excelWriter = EasyExcel.write(response.getOutputStream(), targetClass)
                        .excludeColumnFieldNames(excludedCols)
                        .registerWriteHandler(new EasyExcelWriteHandler(columnSizeList, columnMap, note, lastColIndex))
                        .build();
                excelWriter.write(tempList, writeSheet);
                // 设置单元格高度
                String[] lines = note.split("\r\n|\r|\n");
                excelWriter.writeContext().writeSheetHolder().getSheet().getRow(1).setHeightInPoints(lines.length * 18);
            }
            // 不存在自定义说明
            else {
                excelWriter = EasyExcel.write(response.getOutputStream(), targetClass)
                        .excludeColumnFieldNames(excludedCols)
                        .registerWriteHandler(new EasyExcelWriteHandler(columnSizeList, columnMap, note, lastColIndex))
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .build();
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
     * @param targetClass  待转换的实体类
     * @param dynamicMap   动态下拉框内容, Map of <ExcelDropDown.name, 下拉框内容>
     * @param excludedCols 屏蔽的列名
     * @return Map of <index, 下拉框内容>
     */
    private static Map<Integer, String[]> generateColumnMap(Class<?> targetClass, Map<String, String[]> dynamicMap, Set<String> excludedCols) {
        // 获取 Class 所有字段
        Field[] fieldArray = targetClass.getDeclaredFields();
        Map<Integer, String[]> dropDownMap = new HashMap<>();
        Field field;

        // index 根据排除的列进行调整
        int indexShift = 0;
        // 查找有下拉框注解的字段, 填充相关数据
        for (int index = 0; index < fieldArray.length; index++) {
            field = fieldArray[index];
            // 排除的列, 跳过的同时后续 index 均需要调整
            if (excludedCols.contains(field.getName())) {
                indexShift++;
                continue;
            }

            // 检测到 @ExcelDropDown 注解
            ExcelDropDown excelDropDown = field.getAnnotation(ExcelDropDown.class);
            if (excelDropDown != null) {
                // 使用排除指定列后的 index
                int shiftedIndex = index - indexShift;
                String name = excelDropDown.name();
                // 存在动态下拉框配置
                if (!StringUtils.isEmpty(name) && dynamicMap != null) {
                    // @ExcelDropDown.name 与 dynamicMap.key 相同, 填充内容
                    String[] dynamicOptions = dynamicMap.get(name);
                    if (dynamicOptions != null && dynamicOptions.length > 0) {
                        buildDropDownMap(dropDownMap, dynamicOptions, excelDropDown, shiftedIndex);
                    } else {
                        buildDropDownMap(dropDownMap, null, excelDropDown, shiftedIndex);
                    }
                } else {
                    // 静态下拉框
                    buildDropDownMap(dropDownMap, null, excelDropDown, shiftedIndex);
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

