package com.demo.easyexcel.util;


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
import com.demo.easyexcel.util.annotation.ExcelDropDown;
import com.demo.easyexcel.util.enums.ExcelColWidthEnums;
import com.demo.easyexcel.util.handler.EasyExcelColumnWidthHandler;
import com.demo.easyexcel.util.handler.EasyExcelDropDownMenuHandler;
import com.demo.easyexcel.util.handler.EasyExcelRowHeightHandler;
import com.demo.easyexcel.util.handler.EasyExcelVerticalStyleNoModelHandler;
import com.demo.easyexcel.util.listner.EasyExcelTemplateListener;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateEntity;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateExcelVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * EasyExcel 内部方法类, 不会被外部调用
 *
 * @author Song gh on 2023/3/1.
 */
@Slf4j
public class EasyExcelUtilsProtected {

    // ------------------------------ 常量 ------------------------------
    /** 标题样式 */
    private static final HorizontalCellStyleStrategy titleStrategy;
    /** 自定义说明样式 */
    private static final HorizontalCellStyleStrategy noteStrategy;

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

    // ------------------------------ Public ------------------------------

    /**
     * 导入 Excel 文件, 使用自定义的 ExcelListener 配置
     * <br>1. 返回结果为 true, false, null; 返回 null 时下载含报错信息的 Excel 文件
     *
     * @param file       文件
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param excelClass Excel 类, 必须 extends {@link EasyExcelTemplateExcelVo}
     * @param listener   ExcelListener, 允许 extends {@link EasyExcelTemplateListener}
     * @return true = 成功, false = 文件为空, null = 失败且下载含报错信息的 Excel 文件
     */
    public static <T extends EasyExcelTemplateExcelVo, U extends EasyExcelTemplateListener<T, ? extends EasyExcelTemplateEntity>> Boolean importExcel
    (MultipartFile file, HttpServletRequest request, HttpServletResponse response, Class<T> excelClass, U listener) {
        if (file == null) {
            log.error("上传 Excel 文件为空");
            return false;
        }
        try {
            EasyExcel.read(file.getInputStream(), excelClass, listener).sheet().doRead();
            // head 无效时跳过前 x 行
            while (!listener.getValidHead()) {
                EasyExcel.read(file.getInputStream(), excelClass, listener).headRowNumber(listener.getHeadRowNum()).sheet().doRead();
            }
            // 报错处理
            String fileName = StringUtils.isBlank(file.getOriginalFilename()) ? "" : file.getOriginalFilename();
            String fileNameNoPostfix = fileName.lastIndexOf('.') > 0 ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
            String errorFileName = fileNameNoPostfix + " Excel 导入报错" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            if (!listener.getInvalidList().isEmpty()) {
                // 返回报错信息
                exportExcel(request, response, errorFileName, null, excelClass, listener.getInvalidList(),
                        null, null, null, null, null, null, null);
                return null;
            } else if (listener.getValidExcelList().isEmpty() && listener.getValidEntityList().isEmpty()) {
                // 空白文件或 head 无效
                T tempExcel = excelClass.newInstance();
                tempExcel.setDefaultErrorMessage("无有效列名或导入数据为空");
                List<T> tempExcelList = new LinkedList<>();
                tempExcelList.add(tempExcel);
                exportExcel(request, response, errorFileName, null, excelClass, tempExcelList,
                        null, null, null, null, null, null, null);
                return null;
            }
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            log.error(file.getOriginalFilename() + " Excel 导入异常, 请检查导入文件或 Excel 类 " + excelClass.getName(), e);
        }
        return true;
    }

    /**
     * 导出 Excel, 完整参数
     *
     * @param request            http servlet request
     * @param response           http servlet response
     * @param fileName           文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param sheetName          表名
     * @param excelClass         待转换的实体类
     * @param dataList           表内数据, 不填充传 null 即可
     * @param title              标题
     * @param note               首行说明
     * @param excludedCols       排除的列
     * @param headMap            需要替换的列名, Map({@link ExcelProperty#value}, newHeadName)
     * @param dynamicDropDownMap 动态下拉框内容, Map({@link ExcelDropDown#name}, options)
     * @param widthStrategy      列宽选取方式
     * @param useExcel07         是否使用 07 版 Excel
     */
    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, String fileName, String sheetName,
                                   Class<?> excelClass, List<?> dataList, String title, String note, Set<String> excludedCols,
                                   Map<String, String> headMap, Map<String, String[]> dynamicDropDownMap, ExcelColWidthEnums widthStrategy, Boolean useExcel07) {
        // 设置 header
        setHeader(request, response, fileName, useExcel07);

        // 根据下拉框注解, 填充相关内容, 需要考虑屏蔽的列
        Map<Integer, String[]> dropDownMap = generateColumnMap(excelClass, dynamicDropDownMap, excludedCols);

        // 导出
        downloadExcel(response, sheetName, excelClass, dataList, excludedCols, title, note, dropDownMap, headMap, widthStrategy);
    }

    /**
     * 导出 Excel, 不指定 Excel 类
     *
     * @param request            http servlet request
     * @param response           http servlet response
     * @param fileName           文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param sheetName          表名
     * @param dataList           表内数据, 不填充传 null 即可
     * @param useExcel07         是否使用 07 版 Excel
     * @param title              标题
     * @param note               首行说明
     * @param dynamicDropDownMap 动态下拉框内容, Map({@link ExcelDropDown#name}, options)
     * @param widthStrategy      列宽选取方式
     */
    public static void exportExcelNoModel(HttpServletRequest request, HttpServletResponse response, String fileName, String sheetName,
                                          List<List<String>> headList, List<?> dataList, Boolean useExcel07, String title, String note, Map<String, String[]> dropDownMap,
                                          ExcelColWidthEnums widthStrategy) {
        // 设置 header
        setHeader(request, response, fileName, useExcel07);

        // 根据下拉框注解, 填充相关内容, 需要考虑屏蔽的列
//        Map<Integer, String[]> columnMap = generateColumnMap(excelClass, dynamicDropDownMap, excludedCols);

        // 导出
        downloadExcelNoModel(response, sheetName, headList, dataList, title, note, null, widthStrategy);
    }

    // ------------------------------ Private ------------------------------

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

        // 设置文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
        if (StringUtils.isBlank(fileName)) {
            fileName = "Excel 导出数据" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
        } else if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            fileName = fileName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
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
     * 导出 Excel
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
    private static void downloadExcel(HttpServletResponse response, String sheetName, Class<?> targetClass, List<?> dataList,
                                      Set<String> excludedCols, String title, String note, Map<Integer, String[]> dropDownMap,
                                      Map<String, String> headMap, ExcelColWidthEnums widthStrategy) {
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
            } else {
                excludedCols.add(field.getName());
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

    /**
     * 导出 Excel, 不指定 Excel 类
     *
     * @param response      http servlet response
     * @param sheetName     表名
     * @param dataList      表内数据, 不填充传 null 即可
     * @param title         标题
     * @param note          首行说明
     * @param dropDownMap   动态下拉框, Map(index, options)
     * @param widthStrategy 列宽选取方式
     */
    private static void downloadExcelNoModel(HttpServletResponse response, String sheetName, List<List<String>> headList, List<?> dataList,
                                           String title, String note, Map<Integer, String[]> dropDownMap, ExcelColWidthEnums widthStrategy) {
        // 计数, 未被排除的列
        int validColumnNum = headList.size();

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
            // 下拉框
            int skipRowNum = 0;
            if (StringUtils.isNotBlank(title)) {
                skipRowNum++;
            }
            if (StringUtils.isNotBlank(note)) {
                skipRowNum++;
            }
            writerBuilder.registerWriteHandler(new EasyExcelDropDownMenuHandler(dropDownMap, skipRowNum));
            // 配置 head 样式
            writerBuilder.registerWriteHandler(new EasyExcelVerticalStyleNoModelHandler());
            // 自适应列宽
            writerBuilder.registerWriteHandler(new EasyExcelColumnWidthHandler(widthStrategy));
            // 自适应行高
            writerBuilder.registerWriteHandler(new EasyExcelRowHeightHandler());
            excelWriter = writerBuilder.build();

            // 根据是否存在标题与自定义说明, 配置导出设置
            int mainTableIndex = 0;
            // 单表(不使用标题与自定义说明)且需要动态列名时, 需要在 writer 中应用 head 样式
            // 标题
            if (StringUtils.isNotBlank(title)) {
                setUpTitle(excelWriter, writeSheet, EasyExcelTemplateExcelVo.class, title, validColumnNum, mainTableIndex);
                // tableIndex 顺沿
                mainTableIndex++;
            }
            // 自定义说明
            if (StringUtils.isNotBlank(note)) {
                setUpNote(excelWriter, writeSheet, EasyExcelTemplateExcelVo.class, note, validColumnNum, mainTableIndex);
                // tableIndex 顺沿
                mainTableIndex++;
            }

            // 导出
            WriteTable mainTable;
            mainTable = EasyExcel.writerTable(mainTableIndex).head(headList).needHead(true).build();
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
        // 写入 Excel, 无需 head
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
        // 写入 Excel, 无需 head
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
        if (!Optional.ofNullable(excelDropDown).isPresent()) {
            // 注解为空, 无后续步骤
            return;
        }
        if (dynamicOptions != null && dynamicOptions.length > 0) {
            // 动态下拉框
            dropDownMap.put(index, dynamicOptions);
        } else {
            // 静态下拉框
            String[] source = excelDropDown.value();
            if (source != null && source.length > 0) {
                dropDownMap.put(index, source);
            }
        }
    }
}