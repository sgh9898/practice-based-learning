package com.demo.easyexcel;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.merge.OnceAbsoluteMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.demo.easyexcel.annotation.ExcelDropDown;
import com.demo.easyexcel.enums.EasyExcelColumnWidthEnums;
import com.demo.easyexcel.handler.EasyExcelColumnWidthHandler;
import com.demo.easyexcel.handler.EasyExcelDropDownMenuHandler;
import com.demo.easyexcel.handler.EasyExcelRowHeightHandler;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

import javax.servlet.ServletOutputStream;
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
 *
 * @author Song gh on 2022/7/12.
 */
@Setter
public class EasyExcelUtil {

    /** 标题样式 */
    private static final HorizontalCellStyleStrategy titleStrategy;
    /** 自定义说明样式 */
    private static final HorizontalCellStyleStrategy noteStrategy;

    /** 文件名 */
    private String fileName;
    /** 表名 */
    private String sheetName;
    /** 标题(列名之上) */
    private String title;
    /** 说明行(列名之上, 标题之下) */
    private String note;
    /** Excel 定义类 */
    private Class<?> targetClass;
    /** 导出数据 */
    private List<?> dataList;
    /** 导出时排除的列名 */
    private Set<String> excludedCols;
    /** 需要替换的列名, Map({@link ExcelProperty#value}, newHeadName) */
    private Map<String, String> headMap;
    /** 动态下拉框内容, Map({@link ExcelDropDown#name}, options) */
    private Map<String, String[]> dynamicDropDownMap;

    /** 列宽选取方式 */
    private EasyExcelColumnWidthEnums widthStrategy;
    /** 使用 07 版 Excel */
    private Boolean useExcel07;

    // ------------------------------ 常量 ------------------------------
    static {
        // 标题样式
        WriteCellStyle titleStyle = new WriteCellStyle();
        WriteFont titleFont = new WriteFont();
        titleFont.setFontHeightInPoints((short) 18);
        titleStyle.setWriteFont(titleFont);
        titleStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        titleStrategy = new HorizontalCellStyleStrategy();
        List<WriteCellStyle> titleList = new ArrayList<>();
        titleList.add(titleStyle);
        titleStrategy.setContentWriteCellStyleList(titleList);

        // 自定义说明样式
        WriteCellStyle noteStyle = new WriteCellStyle();
        WriteFont noteFont = new WriteFont();
        noteFont.setColor(IndexedColors.CORAL.getIndex());
        noteFont.setFontHeightInPoints((short) 12);
        noteStyle.setWriteFont(noteFont);
        noteStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        noteStrategy = new HorizontalCellStyleStrategy();
        List<WriteCellStyle> noteList = new ArrayList<>();
        noteList.add(noteStyle);
        noteStrategy.setContentWriteCellStyleList(noteList);
    }

    /** Constructor: 基础配置 */
    public EasyExcelUtil(String fileName, Class<?> targetClass, List<?> dataList) {
        this.fileName = fileName;
        this.targetClass = targetClass;
        this.dataList = dataList;
    }

// ------------------------------ Static ------------------------------

    /** 浏览器下载 Excel: 基础, 仅指定文件名 */
    public static void downloadSimple(HttpServletRequest request, HttpServletResponse response, String fileName,
                                      Class<?> targetClass, List<?> dataList) {
        downloadExcel(request, response, fileName, null, targetClass, dataList,
                null, null, null, null, null, null, null);
    }

    /** 浏览器下载 Excel: 排除指定列 */
    public static void downloadExcluding(HttpServletRequest request, HttpServletResponse response, String fileName,
                                         Class<?> targetClass, List<?> dataList, Set<String> excludedCols) {
        downloadExcel(request, response, fileName, null, targetClass, dataList,
                null, null, null, excludedCols, null, null, null);
    }

    /** 浏览器下载 Excel 模板: 数据为空, 添加说明, 排除指定列 */
    public static void downloadTemplate(HttpServletRequest request, HttpServletResponse response, String fileName,
                                        Class<?> targetClass, String note, Set<String> excludedCols) {
        downloadExcel(request, response, fileName, null, targetClass, null,
                null, null, note, excludedCols, null, null, EasyExcelColumnWidthEnums.COLUMN_WIDTH_USE_HEAD);
    }

    /** 获取指定的 Class 全部 {@link ExcelProperty} 列名 */
    public static Set<String> getHeadNameSet(Class<?> targetClass) {
        // 记录列名, 用于动态列名替换
        Set<String> headNameSet = new HashSet<>();
        for (Field field : targetClass.getDeclaredFields()) {
            ExcelProperty excelAnnotation = field.getAnnotation(ExcelProperty.class);
            if (excelAnnotation != null) {
                headNameSet.addAll(Arrays.asList(excelAnnotation.value()));
            }
        }
        return headNameSet;
    }

    /** 获取需要屏蔽的列 -- by 需要保留的列(为空时不生效) */
    public static Set<String> getExcludedCols(Class<?> targetClass, Set<String> includedCols) {
        if (includedCols == null || includedCols.isEmpty()) {
            return new HashSet<>();
        }
        // 获取 Class 所有字段
        Set<String> excludedCols = new HashSet<>();
        Field[] fieldArray = targetClass.getDeclaredFields();
        for (Field field : fieldArray) {
            if (!includedCols.contains(field.getName())) {
                excludedCols.add(field.getName());
            }
        }
        return excludedCols;
    }

// ------------------------------ Non-Static ------------------------------

    /** 浏览器下载 Excel: 使用当前自定义配置 */
    public void downloadCustomizedExcel(HttpServletRequest request, HttpServletResponse response) {
        downloadExcel(request, response, fileName, sheetName, targetClass, dataList,
                useExcel07, title, note, excludedCols, headMap, dynamicDropDownMap, widthStrategy);
    }

// ------------------------------ Private ------------------------------

    /**
     * 浏览器下载 Excel, 完整参数
     *
     * @param request            http servlet request
     * @param response           http servlet response
     * @param fileName           文件名
     * @param sheetName          表名
     * @param targetClass        待转换的实体类
     * @param dataList           表内数据, 不填充传 null 即可
     * @param useExcel07         是否使用 07 版 Excel
     * @param title              标题
     * @param note               首行说明
     * @param excludedCols       排除的列
     * @param headMap            需要替换的列名, Map({@link ExcelProperty#value}, newHeadName)
     * @param dynamicDropDownMap 动态下拉框内容, Map({@link ExcelDropDown#name}, options)
     * @param widthStrategy      列宽选取方式
     */
    private static void downloadExcel(HttpServletRequest request, HttpServletResponse response,
                                      String fileName, String sheetName, Class<?> targetClass, List<?> dataList,
                                      Boolean useExcel07, String title, String note, Set<String> excludedCols,
                                      Map<String, String> headMap, Map<String, String[]> dynamicDropDownMap,
                                      EasyExcelColumnWidthEnums widthStrategy) {
        // 设置 header
        setHeader(request, response, fileName, useExcel07);

        // 根据下拉框注解, 填充相关内容, 需要考虑屏蔽的列
        Map<Integer, String[]> columnMap = generateColumnMap(targetClass, dynamicDropDownMap, excludedCols);

        // 导出
        exportExcel(response, sheetName, targetClass, dataList, excludedCols, title, note, columnMap, headMap, widthStrategy);
    }

    /** 配置 header */
    private static void setHeader(HttpServletRequest request, HttpServletResponse response, String fileName, Boolean useExcel07) {
        // 设置文件格式, 编码
        if (useExcel07 == Boolean.TRUE) {
            // 07 版 Excel, 可以解决部分 Excel 不兼容问题
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } else {
            // 03 版 Excel
            response.setContentType("application/vnd.ms-excel");
        }
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
     * @param response      http servlet response
     * @param sheetName     表名
     * @param targetClass   待转换的实体类
     * @param dataList      表内数据, 不填充传 null 即可
     * @param excludedCols  排除的列
     * @param title         标题
     * @param note          首行说明
     * @param dropDownMap   动态下拉框, Map(index, options)
     * @param headMap       需要替换的列名, Map({@link ExcelProperty#value}, newHeadName)
     * @param widthStrategy 列宽选取方式
     */
    private static void exportExcel(HttpServletResponse response, String sheetName, Class<?> targetClass, List<?> dataList,
                                    Set<String> excludedCols, String title, String note, Map<Integer, String[]> dropDownMap, Map<String, String> headMap,
                                    EasyExcelColumnWidthEnums widthStrategy) {
        // 防报错
        if (excludedCols == null) {
            excludedCols = new HashSet<>();
        }

        // 列名排除与动态列名
        // 计数, 未被排除的列
        int validColumnNum = 0;
        // 记录列名, 用于动态列名替换
        List<String[]> originalHeadList = new ArrayList<>();
        for (Field field : targetClass.getDeclaredFields()) {
            ExcelProperty excelAnnotation = field.getAnnotation(ExcelProperty.class);
            if (excelAnnotation != null) {
                // 未排除的列
                if (!excludedCols.contains(field.getName())) {
                    // 记录列名, 用于动态列名替换
                    originalHeadList.add(excelAnnotation.value());
                    // 统计总数
                    validColumnNum++;
                }
            }
        }

        // 动态列名
        boolean replaceHead = false;
        List<List<String>> newHeadList = new ArrayList<>();
        if (headMap != null && !headMap.isEmpty()) {
            for (String[] headArray : originalHeadList) {
                for (int i = 0; i < headArray.length; i++) {
                    String newHead = headMap.get(headArray[i]);
                    if (StringUtils.isNotBlank(newHead)) {
                        headArray[i] = newHead;
                        replaceHead = true;
                    }
                }
                newHeadList.add(new ArrayList<>(Arrays.asList(headArray)));
            }
        }

        // 导出
        ExcelWriter excelWriter = null;
        ServletOutputStream outputStream = null;
        try {
            // 默认表格名
            if (StringUtils.isBlank(sheetName)) {
                sheetName = "Sheet1";
            }

            // 创建表格
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            outputStream = response.getOutputStream();
            ExcelWriterBuilder writerBuilder = EasyExcel.write(outputStream).excelType(ExcelTypeEnum.XLSX);
            // 需要排除的列
            writerBuilder.excludeColumnFieldNames(excludedCols);
            // 下拉框
            int skipRowNum = 0;
            if (StringUtils.isNotBlank(title)) {
                skipRowNum++;
            }
            if (StringUtils.isNotBlank(note)) {
                skipRowNum++;
            }
            writerBuilder.registerWriteHandler(new EasyExcelDropDownMenuHandler(dropDownMap, skipRowNum));
            // 自适应列宽
            writerBuilder.registerWriteHandler(new EasyExcelColumnWidthHandler(widthStrategy));
            // 自适应行高
            writerBuilder.registerWriteHandler(new EasyExcelRowHeightHandler());
            excelWriter = writerBuilder.build();

            // 根据是否存在标题与自定义说明, 配置导出设置
            int mainTableIndex = 0;
            // 单表(不使用标题与自定义说明)且需要动态列名时, 需要在 writer 中应用 head 样式
            if (replaceHead && StringUtils.isBlank(title) && StringUtils.isBlank(note)) {
                excelWriter = writerBuilder.head(targetClass).head(newHeadList).build();
                excelWriter.write(dataList, writeSheet);
                return;
            } else {
                // 标题
                if (StringUtils.isNotBlank(title)) {
                    setUpTitle(excelWriter, writeSheet, targetClass, title, validColumnNum, mainTableIndex);
                    // tableIndex 顺沿
                    mainTableIndex++;
                }
                // 自定义说明
                if (StringUtils.isNotBlank(note)) {
                    setUpNote(excelWriter, writeSheet, targetClass, note, validColumnNum, mainTableIndex);
                    // tableIndex 顺沿
                    mainTableIndex++;
                }
            }

            WriteTable mainTable;
            if (replaceHead) {
                // 动态列名
                mainTable = EasyExcel.writerTable(mainTableIndex).head(targetClass).head(newHeadList).needHead(true).build();
            } else {
                // 常规列名
                mainTable = EasyExcel.writerTable(mainTableIndex).head(targetClass).needHead(true).build();
            }
            excelWriter.write(dataList, writeSheet, mainTable);
        } catch (IOException e) {
            throw new RuntimeException("Excel 导出失败");
        } finally {
            // 关闭所有的流
            if (excelWriter != null) {
                excelWriter.finish();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** 导出时配置标题 */
    private static void setUpTitle(ExcelWriter excelWriter, WriteSheet writeSheet, Class<?> targetClass, String title, int validColumnNum, int mainTableIndex) {
        // 配置说明文字
        List<List<Object>> titleContent = new ArrayList<>();
        List<Object> titleLine = new ArrayList<>();
        titleLine.add(title);
        titleContent.add(titleLine);

        // 合并, index 同时代表当前行
        OnceAbsoluteMergeStrategy mergeTitleRow = new OnceAbsoluteMergeStrategy(mainTableIndex, mainTableIndex, 0, validColumnNum - 1);
        // 写入 Excel, 无需表头
        WriteTable noteTable = EasyExcel.writerTable(mainTableIndex)
                .registerWriteHandler(mergeTitleRow).head(targetClass)
                .registerWriteHandler(titleStrategy).needHead(false).build();
        excelWriter.write(titleContent, writeSheet, noteTable);
    }

    /** 导出时配置首行说明 */
    private static void setUpNote(ExcelWriter excelWriter, WriteSheet writeSheet, Class<?> targetClass, String note, int validColumnNum, int mainTableIndex) {
        // 配置说明文字
        List<List<Object>> noteContent = new ArrayList<>();
        List<Object> noteLine = new ArrayList<>();
        noteLine.add(note);
        noteContent.add(noteLine);

        // 合并首行
        OnceAbsoluteMergeStrategy mergeNoteRow = new OnceAbsoluteMergeStrategy(mainTableIndex, mainTableIndex, 0, validColumnNum - 1);
        // 写入 Excel, 无需表头
        WriteTable noteTable = EasyExcel.writerTable(mainTableIndex)
                .registerWriteHandler(mergeNoteRow).head(targetClass)
                .registerWriteHandler(noteStrategy).needHead(false).build();
        excelWriter.write(noteContent, writeSheet, noteTable);
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
        int indexLeftShift = 0;
        // 查找有下拉框注解的字段, 填充相关数据
        for (int index = 0; index < fieldArray.length; index++) {
            field = fieldArray[index];
            // 排除的列, 跳过的同时后续 index 均需要调整
            if (excludedCols != null && excludedCols.contains(field.getName())) {
                indexLeftShift++;
                continue;
            }

            // 检测到 @ExcelDropDown 注解
            ExcelDropDown excelDropDown = field.getAnnotation(ExcelDropDown.class);
            if (excelDropDown != null) {
                // 使用排除指定列后的 index
                int shiftedIndex = index - indexLeftShift;
                String name = excelDropDown.name();
                // 存在动态下拉框配置
                if (!StringUtils.isEmpty(name) && dynamicMap != null) {
                    // {@link ExcelDropDown#name} 与 dynamicMap.key 相同, 填充内容
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
            String[] source = excelDropDown.value();
            if (source != null && source.length > 0) {
                dropDownMap.put(index, source);
            }
        }
    }
}