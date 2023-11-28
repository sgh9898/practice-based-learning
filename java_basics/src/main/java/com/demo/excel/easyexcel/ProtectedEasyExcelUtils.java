package com.demo.excel.easyexcel;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.builder.ExcelWriterTableBuilder;
import com.alibaba.excel.write.merge.OnceAbsoluteMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.multipart.MultipartFile;

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
 * EasyExcel 基础方法类, 不会被外部调用
 * <br> 1. Excel 导入:
 * <br>     1) 指定 ExcelClass: {@link #baseImportExcel}
 * <br>     2) 不指定 ExcelClass: {@link #noModelBaseImportExcel}
 * <br> 2. Excel 普通导出:
 * <br>     1) 普通导出: {@link #baseExportExcel}
 * <br>     2) 不指定 ExcelClass, 普通导出: {@link #noModelBaseExportExcel}
 * <br> 3. Excel 自定义导出, 最后均需要手动调用 {@link #closeExcelWriter} 进行关闭:
 * <br>     1) 写入指定 Sheet: {@link #baseWriteSheet}
 * <br>     2) 不指定 ExcelClass, 写入指定 Sheet: {@link #noModelBaseWriteSheet}
 *
 * @author Song gh on 2023/3/1.
 */
@Slf4j
class ProtectedEasyExcelUtils {

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
// ============================== 常量 End ==============================

// ------------------------------ Public, 导入 ------------------------------

    /**
     * 导入 Excel 文件, 使用自定义的 ExcelListener 配置
     *
     * @param file        文件
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     * @param excelClass  Excel 类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param listener    ExcelListener, 允许 extends {@link Listener}
     * @param exportError 自动导出报错
     * @param errorList   不自动导出时用于保存报错(exportError == false)
     * @return true = 成功, false = 文件为空, null = 失败且下载含报错信息的 Excel 文件
     */
    public static <T, U extends Listener<T>> Boolean baseImportExcel(MultipartFile file, HttpServletRequest request, HttpServletResponse response,
                                                                     Class<T> excelClass, U listener, Boolean exportError, List<T> errorList) {
        if (file == null) {
            log.error("上传 Excel 文件为空");
            return false;
        }
        try {
            // 读取数据
            EasyExcel.read(file.getInputStream(), excelClass, listener).extraRead(CellExtraTypeEnum.MERGE).sheet().doRead();
            // head 无效时跳过前 x 行
            while (!listener.getValidHead()) {
                EasyExcel.read(file.getInputStream(), excelClass, listener).extraRead(CellExtraTypeEnum.MERGE).headRowNumber(listener.getHeadRowNum()).sheet().doRead();
            }

            // 报错处理
            String fileName = StringUtils.isBlank(file.getOriginalFilename()) ? "" : file.getOriginalFilename();
            String fileNameNoPostfix = fileName.lastIndexOf('.') > 0 ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
            String errorFileName = fileNameNoPostfix + " Excel 导入报错" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            // 存在错误信息, 且需要导出
            if (!listener.getInvalidList().isEmpty() && request != null && response != null) {
                if (exportError) {
                    // 导出包含报错的 Excel
                    baseExportExcel(request, response, excelClass, errorFileName, null, listener.getInvalidList(),
                            null, null, null, null, null, null, null);
                    return null;
                } else {
                    // 仅保存报错信息
                    if (errorList != null) {
                        errorList.addAll(listener.getInvalidList());
                    }
                }
            }
        } catch (IOException e) {
            log.error(file.getOriginalFilename() + " Excel 导入异常, 请检查导入文件或 Excel 类 " + excelClass.getName(), e);
            return null;
        }
        return true;
    }

    /**
     * 不指定 ExcelClass 导入 Excel 文件, 使用自定义的 ExcelListener 配置
     * <br>1. 返回结果为 true, false, null; 返回 null 时下载含报错信息的 Excel 文件
     *
     * @param file        文件
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     * @param exportError 自动导出报错
     * @param errorList   不自动导出时用于保存报错(exportError == false)
     * @return true = 成功, false = 文件为空, null = 失败且下载含报错信息的 Excel 文件
     */
    public static <T extends ListenerNoModel> Boolean noModelBaseImportExcel(MultipartFile file, HttpServletRequest request, HttpServletResponse response,
                                                                             T listener, Boolean exportError, List<List<Object>> errorList) {
        if (file == null) {
            log.error("上传 Excel 文件为空");
            return false;
        }
        try {
            // 读取数据
            EasyExcel.read(file.getInputStream(), listener).extraRead(CellExtraTypeEnum.MERGE).sheet().doRead();
            // head 无效时跳过前 x 行
            while (!listener.getValidHead()) {
                EasyExcel.read(file.getInputStream(), listener).extraRead(CellExtraTypeEnum.MERGE).headRowNumber(listener.getHeadRowNum()).sheet().doRead();
            }

            // 报错处理
            String fileName = StringUtils.isBlank(file.getOriginalFilename()) ? "" : file.getOriginalFilename();
            String fileNameNoPostfix = fileName.lastIndexOf('.') > 0 ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
            String errorFileName = fileNameNoPostfix + " Excel 导入报错" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            List<List<String>> outerHeadList = new LinkedList<>();
            for (int i = 0; i < listener.getIndexedCnHeadMap().size(); i++) {
                List<String> innerHeadList = new LinkedList<>();
                innerHeadList.add(listener.getIndexedCnHeadMap().get(i));
                outerHeadList.add(innerHeadList);
            }
            List<String> innerHeadList = new LinkedList<>();
            innerHeadList.add("错误信息");
            outerHeadList.add(innerHeadList);
            // 存在错误信息
            if (!listener.getInvalidList().isEmpty()) {
                if (exportError) {
                    // 导出包含报错的 Excel
                    noModelBaseExportExcel(request, response, errorFileName, "Sheet1", outerHeadList, listener.getInvalidList(),
                            null, null, null, null, null);
                    return null;
                } else {
                    // 仅保存报错信息
                    if (errorList != null) {
                        errorList.addAll(listener.getInvalidList());
                    }
                }
            }
        } catch (IOException e) {
            log.error(file.getOriginalFilename() + " Excel 导入异常, 请检查导入文件 ", e);
            return null;
        }
        return true;
    }
// ============================== Public, 导入 End ==============================

// ------------------------------ Public, 导出 ------------------------------

    /**
     * 导出 Excel
     *
     * @param request            http servlet request
     * @param response           http servlet response
     * @param excelClass         待转换的实体类
     * @param fileName           [允许空/null] 文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param sheetName          [允许空/null] 表名
     * @param dataList           [允许空/null] 表内数据, 不填充传 null 即可
     * @param title              [允许空/null] 标题, 在自定义说明之上
     * @param note               [允许空/null] 自定义说明, 在列名之上
     * @param excludedCols       [允许空/null] 排除的列
     * @param headMap            [允许空/null] 需要替换的列名, Map({@link ExcelProperty#value}, newHeadName)
     * @param dynamicDropDownMap [允许空/null] 动态下拉框内容, Map({@link ExcelDropDown#name}, options)
     * @param widthStrategy      [允许空/null] 列宽选取方式
     * @param useExcel07         [允许空/null] 是否使用 07 版 Excel
     */
    public static void baseExportExcel(HttpServletRequest request, HttpServletResponse response, Class<?> excelClass, String fileName, String sheetName,
                                       List<?> dataList, String title, String note, Set<String> excludedCols, Map<String, String> headMap,
                                       Map<String, String[]> dynamicDropDownMap, ProtectedEnumsColWidth widthStrategy, Boolean useExcel07) {
        ExcelWriter excelWriter = createExcelWriter(request, response, fileName, useExcel07);
        baseWriteSheet(excelWriter, excelClass, 0, sheetName, dataList, excludedCols, title, note, dynamicDropDownMap, headMap, widthStrategy);
        closeExcelWriter(response, excelWriter);
    }

    /**
     * [不指定 ExcelClass] 导出 Excel
     *
     * @param request       http servlet request
     * @param response      http servlet response
     * @param fileName      [允许空/null] 文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param sheetName     [允许空/null] 表名
     * @param cnHeadList    列名
     * @param dataList      [允许空/null] 表内数据, 不填充传 null 即可
     * @param title         [允许空/null] 标题, 在自定义说明之上
     * @param note          [允许空/null] 自定义说明, 在列名之上
     * @param dropDownMap   [允许空/null] 下拉框内容, Map(列序号, 选项)
     * @param widthStrategy [允许空/null] 列宽选取方式
     * @param useExcel07    [允许空/null] 是否使用 07 版 Excel
     */
    public static void noModelBaseExportExcel(HttpServletRequest request, HttpServletResponse response, String fileName, String sheetName,
                                              List<List<String>> cnHeadList, List<List<Object>> dataList, String title, String note,
                                              Map<Integer, String[]> dropDownMap, ProtectedEnumsColWidth widthStrategy, Boolean useExcel07) {
        ExcelWriter excelWriter = createExcelWriter(request, response, fileName, useExcel07);
        noModelBaseWriteSheet(excelWriter, 0, sheetName, cnHeadList, null, dataList, title, note, dropDownMap, widthStrategy);
        closeExcelWriter(response, excelWriter);
    }

    /**
     * Excel 导出: 写入单张 Sheet(导出完成后需手动调用 {@link #closeExcelWriter} 关闭流)
     *
     * @param excelWriter        Excel 主体
     * @param excelClass         待转换的实体类
     * @param sheetIndex         表序号
     * @param sheetName          [允许空/null] 表名, 单个文件中不允许出现重复表名(null 时会有默认表名)
     * @param dataList           [允许空/null] 表内数据
     * @param excludedCols       [允许空/null] 排除的列
     * @param title              [允许空/null] 标题, 在自定义说明之上
     * @param note               [允许空/null] 自定义说明, 在列名之上
     * @param dynamicDropDownMap [允许空/null] 动态下拉框, Map(index, options)
     * @param replaceHeadMap     [允许空/null] 需要替换的列名, Map({@link ExcelProperty#value}, newHeadName)
     * @param widthStrategy      [允许空/null] 列宽选取方式
     */
    public static void baseWriteSheet(ExcelWriter excelWriter, Class<?> excelClass, Integer sheetIndex, String sheetName, List<?> dataList,
                                      Set<String> excludedCols, String title, String note, Map<String, String[]> dynamicDropDownMap,
                                      Map<String, String> replaceHeadMap, ProtectedEnumsColWidth widthStrategy) {
        // 防报错
        if (excludedCols == null) {
            excludedCols = new HashSet<>();
        }
        // 处理下拉框
        Map<Integer, String[]> dropDownMap = generateColumnMap(excelClass, dynamicDropDownMap, excludedCols);

        // 列名排除与动态列名
        // 计数, 未被排除的列
        int validColumnNum = 0;
        // 记录列名, 用于动态列名替换
        List<String[]> originalHeadList = new ArrayList<>();
        // 记录手动指定列宽的字段
        Set<String> doNotChangeWidth = new HashSet<>();
        for (Field field : excelClass.getDeclaredFields()) {
            ExcelProperty excelAnnotation = field.getAnnotation(ExcelProperty.class);
            if (excelAnnotation != null) {
                // 未排除的列
                if (!excludedCols.contains(field.getName())) {
                    // 记录列名, 用于动态列名替换
                    originalHeadList.add(excelAnnotation.value());
                    // 统计总数
                    validColumnNum++;
                    // 记录手动指定的列宽
                    if (excelClass.getAnnotation(ColumnWidth.class) != null || field.getAnnotation(ColumnWidth.class) != null) {
                        doNotChangeWidth.add(field.getName());
                    }
                }
            } else {
                excludedCols.add(field.getName());
            }
        }

        // 动态列名
        boolean replaceHead = false;
        List<List<String>> newHeadList = new ArrayList<>();
        if (replaceHeadMap != null && !replaceHeadMap.isEmpty()) {
            for (String[] headArray : originalHeadList) {
                for (int i = 0; i < headArray.length; i++) {
                    String newHead = replaceHeadMap.get(headArray[i]);
                    if (StringUtils.isNotBlank(newHead)) {
                        headArray[i] = newHead;
                        replaceHead = true;
                    }
                }
                newHeadList.add(new ArrayList<>(Arrays.asList(headArray)));
            }
        }

        // 创建表单
        ExcelWriterSheetBuilder sheetBuilder = EasyExcel.writerSheet(sheetIndex, sheetName);
        // 需要排除的列
        sheetBuilder.excludeColumnFieldNames(excludedCols);
        // 下拉框
        int skipRowNum = 0;
        if (StringUtils.isNotBlank(title)) {
            skipRowNum++;
        }
        if (StringUtils.isNotBlank(note)) {
            skipRowNum++;
        }
        sheetBuilder.registerWriteHandler(new ProtectedHandlerDropDownMenu(dropDownMap, skipRowNum));
        // 自适应列宽
        sheetBuilder.registerWriteHandler(new ProtectedHandlerColumnWidth(widthStrategy, doNotChangeWidth));
        // 自适应行高
        sheetBuilder.registerWriteHandler(new ProtectedHandlerRowHeight());

        // 根据是否存在标题与自定义说明, 配置导出设置
        int mainTableIndex = 0;
        // 单表(不使用标题与自定义说明)且需要动态列名时, 需要在 writer 中应用 head 样式
        if (replaceHead && StringUtils.isBlank(title) && StringUtils.isBlank(note)) {
            sheetBuilder.head(excelClass).head(newHeadList).build();
            excelWriter.write(dataList, sheetBuilder.build());
            return;
        } else {
            // 标题
            if (StringUtils.isNotBlank(title)) {
                writeTitle(excelWriter, sheetBuilder.build(), excelClass, title, validColumnNum, mainTableIndex);
                // tableIndex 顺沿
                mainTableIndex++;
            }
            // 自定义说明
            if (StringUtils.isNotBlank(note)) {
                writeNote(excelWriter, sheetBuilder.build(), excelClass, note, validColumnNum, mainTableIndex);
                // tableIndex 顺沿
                mainTableIndex++;
            }
        }

        WriteTable mainTable;
        if (replaceHead) {
            // 动态列名
            mainTable = EasyExcel.writerTable(mainTableIndex).head(excelClass).head(newHeadList).needHead(true).build();
        } else {
            // 常规列名
            mainTable = EasyExcel.writerTable(mainTableIndex).head(excelClass).needHead(true).build();
        }
        excelWriter.write(dataList, sheetBuilder.build(), mainTable);
    }

    /**
     * [不指定 ExcelClass] Excel 导出: 写入单张 Sheet(导出完成后需手动调用 {@link #closeExcelWriter} 关闭流)
     *
     * @param excelWriter    Excel 导出主体
     * @param sheetIndex     [允许 null] 表序号
     * @param sheetName      [允许空/null] 表名
     * @param cnHeadList     中文列名
     * @param specialHeadSet [允许空/null] 需要标红的列名(中文)
     * @param dataList       [允许空/null] 表内数据, 不填充传 null 即可
     * @param title          [允许空/null] 标题, 在自定义说明之上
     * @param note           [允许空/null] 自定义说明, 在列名之上
     * @param dropDownMap    [允许空/null] 下拉框内容, Map(列序号, 选项)
     * @param widthStrategy  [允许空/null] 列宽选取方式
     */
    public static void noModelBaseWriteSheet(ExcelWriter excelWriter, Integer sheetIndex, String sheetName, List<List<String>> cnHeadList, Set<String> specialHeadSet, List<List<Object>> dataList,
                                             String title, String note, Map<Integer, String[]> dropDownMap, ProtectedEnumsColWidth widthStrategy) {
        // 计数, 未被排除的列
        int validColumnNum = cnHeadList.size();

        // 导出
        // 创建表格
        ExcelWriterSheetBuilder sheetBuilder = EasyExcel.writerSheet(sheetIndex, sheetName);
        // 下拉框
        int skipRowNum = 0;
        if (StringUtils.isNotBlank(title)) {
            skipRowNum++;
        }
        if (StringUtils.isNotBlank(note)) {
            skipRowNum++;
        }
        int headRows = 0;
        for (List<String> headColumn : cnHeadList) {
            headRows = Math.max(headRows, headColumn.size());
        }
        skipRowNum = skipRowNum + headRows - 1;
        sheetBuilder.registerWriteHandler(new ProtectedHandlerDropDownMenu(dropDownMap, skipRowNum));
        // 配置 head 样式
        sheetBuilder.registerWriteHandler(new ProtectedHandlerVerticalStyleNoModel(specialHeadSet));
        // 自适应列宽
        sheetBuilder.registerWriteHandler(new ProtectedHandlerColumnWidth(widthStrategy));
        // 自适应行高
        sheetBuilder.registerWriteHandler(new ProtectedHandlerRowHeight());

        // 根据是否存在标题与自定义说明, 配置导出设置
        int mainTableIndex = 0;
        // 单表(不使用标题与自定义说明)且需要动态列名时, 需要在 writer 中应用 head 样式
        // 标题
        if (StringUtils.isNotBlank(title)) {
            writeTitle(excelWriter, sheetBuilder.build(), EasyExcelClassTemplate.class, title, validColumnNum, mainTableIndex);
            // tableIndex 顺沿
            mainTableIndex++;
        }
        // 自定义说明
        if (StringUtils.isNotBlank(note)) {
            writeNote(excelWriter, sheetBuilder.build(), EasyExcelClassTemplate.class, note, validColumnNum, mainTableIndex);
            // tableIndex 顺沿
            mainTableIndex++;
        }

        // 导出
        WriteTable mainTable;
        mainTable = EasyExcel.writerTable(mainTableIndex).head(cnHeadList).needHead(true).build();
        excelWriter.write(dataList, sheetBuilder.build(), mainTable);
    }
// ============================== Public, 导出 End ==============================

// ------------------------------ Public, 工具 ------------------------------

    /** 创建 Excel Writer */
    public static ExcelWriter createExcelWriter(HttpServletRequest request, HttpServletResponse response, String fileName, Boolean useExcel07) {
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

        // 生成 excel writer
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(response.getOutputStream()).excelType(ExcelTypeEnum.XLSX).build();
        } catch (IOException e) {
            log.error("Excel Writer 创建失败", e);
            closeExcelWriter(response, null);
        }
        return excelWriter;
    }

    /** 关闭 Excel Writer 以及输出流 */
    public static void closeExcelWriter(HttpServletResponse response, ExcelWriter excelWriter) {
        // 关闭 excel writer
        if (excelWriter != null) {
            excelWriter.finish();
        }
        // 关闭输出流
        try {
            if (response.getOutputStream() != null) {
                response.getOutputStream().close();
            }
        } catch (IOException e) {
            log.error("关闭输出流失败", e);
            throw new RuntimeException(e);
        }
    }
// ============================== Public, 工具 End ==============================

// ------------------------------ Private ------------------------------

    /** Excel 导出时创建 Sheet 标题, 在自定义说明之上 */
    private static void writeTitle(ExcelWriter excelWriter, WriteSheet writeSheet, Class<?> targetClass, String title, int validColumnNum, int mainTableIndex) {
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

    /** Excel 导出时创建 Sheet 自定义说明, 在列名之上 */
    private static void writeNote(ExcelWriter excelWriter, WriteSheet writeSheet, Class<?> targetClass, String note, int validColumnNum, int mainTableIndex) {
        // 配置说明文字
        List<List<Object>> noteContent = new ArrayList<>();
        List<Object> noteLine = new ArrayList<>();
        noteLine.add(note);
        noteContent.add(noteLine);

        // 写入 Excel
        ExcelWriterTableBuilder tableBuilder = EasyExcel.writerTable(mainTableIndex).head(targetClass).registerWriteHandler(noteStrategy).needHead(false);
        // 合并首行
        if (validColumnNum > 1) {
            OnceAbsoluteMergeStrategy mergeNoteRow = new OnceAbsoluteMergeStrategy(mainTableIndex, mainTableIndex, 0, validColumnNum - 1);
            tableBuilder.registerWriteHandler(mergeNoteRow);
        }
        WriteTable noteTable = tableBuilder.build();
        excelWriter.write(noteContent, writeSheet, noteTable);
    }

    /**
     * 处理 @ExcelDropDown 注解
     *
     * @param targetClass  待转换的实体类
     * @param dynamicMap   动态下拉框内容, Map({@link ExcelDropDown#name}, 下拉框内容)
     * @param excludedCols 屏蔽的列名
     * @return Map(index, 下拉框内容)
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
                String[] dynamicOptions = excelDropDown.value();
                String name = excelDropDown.name();
                // 存在动态下拉框配置
                if (!StringUtils.isEmpty(name) && dynamicMap != null) {
                    // {@link ExcelDropDown#name} 与 dynamicMap.key 相同, 填充内容
                    if (dynamicMap.get(name) != null && dynamicMap.get(name).length > 0) {
                        dynamicOptions = dynamicMap.get(name);
                    }
                }
                dropDownMap.put(shiftedIndex, dynamicOptions);
            }
        }
        return dropDownMap;
    }
}