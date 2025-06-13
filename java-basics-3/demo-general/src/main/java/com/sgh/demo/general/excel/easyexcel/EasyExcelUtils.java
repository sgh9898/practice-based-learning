package com.sgh.demo.general.excel.easyexcel;

import com.alibaba.excel.EasyExcelFactory;
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
import com.sgh.demo.general.excel.easyexcel.annotation.ExcelDropDown;
import com.sgh.demo.general.excel.easyexcel.constants.ExcelConstants;
import com.sgh.demo.general.excel.easyexcel.constants.ExcelHeadRulesEnums;
import com.sgh.demo.general.excel.easyexcel.handler.*;
import com.sgh.demo.general.excel.easyexcel.listener.ExcelListener;
import com.sgh.demo.general.excel.easyexcel.listener.ExcelNoModelListener;
import com.sgh.demo.general.excel.easyexcel.pojo.EasyExcelExportDTO;
import com.sgh.demo.general.excel.easyexcel.pojo.EasyExcelNoModelExportDTO;
import com.sgh.demo.general.excel.easyexcel.pojo.ExcelCascadeOption;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * EasyExcel 工具类
 * <pre>
 * [使用自定义的实体类 ExcelClass]
 *   0. 前置需求: {@code extends} {@link EasyExcelClassTemplate}, 或将类注解与"defaultExcelErrorMessage"字段直接配置于实体类中
 *   1. 导入:
 *     * 导入并暂存报错的数据: {@link #importExcelSaveError}
 *     * 导入并下载报错的数据: {@link #importExcel}
 *   2. 导出:
 *     * 导出空白模板: {@link #exportTemplate}
 *     * 导出数据: {@link #exportData}, 默认的"报错信息"列会被屏蔽
 *     * 导出报错数据: {@link #exportErrorExcel}, 不再屏蔽默认的"报错信息"列
 *   3. 自定义导出:
 *     1) 配置参数(部分未列出): {@link EasyExcelExportDTO}
 *     2) 导出:
 *       * 常规导出: {@link #exportLocalExcel}, 默认的"报错信息"列会被屏蔽
 *       * 分页导出: {@link #writeSheet}--在新的一页写入数据, 数据写入全部完成后必须手动调用 {@link #closeExcel} 进行关闭
 *       * 导出报错数据: {@link #exportErrorExcel}, 不再屏蔽默认的"报错信息"列
 *
 * [不使用自定义的实体类 ExcelClass]
 *   1. 导入:
 *     * 导入数据: {@link #noModelImportExcel}
 *   2. 导出:
 *     * 导出空白模板: {@link #noModelExportTemplate}
 *     * 导出数据: {@link #noModelExportData}
 *   3. 自定义导出:
 *     1) 配置常用参数(部分未列出): {@link EasyExcelNoModelExportDTO}
 *     2) 导出:
 *       * 常规导出: {@link #noModelExportExcel}
 *       * 分页导出: {@link #noModelWriteSheet}--在新的一页写入数据, 数据写入全部完成后必须手动调用 {@link #closeExcel} 进行关闭 </pre>
 *
 * @author Song gh
 * @version 2025/1/10
 */
public class EasyExcelUtils {

    private static final Logger log = LoggerFactory.getLogger(EasyExcelUtils.class);

// ------------------------------ 内部配置(不可修改) ------------------------------

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

    /** Excel 数据写入/编辑工具 */
    private final ExcelWriter excelWriter;

    /** 当前使用的输出流 */
    private final OutputStream outStream;

    /** 表序号 */
    private Integer sheetIndex = 1;

// ------------------------------ 构造 ------------------------------

    /**
     * [构造] 用于 http(s) 的工具
     *
     * @param request  http servlet request
     * @param response http servlet response
     * @param fileName 文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     */
    public EasyExcelUtils(HttpServletRequest request, HttpServletResponse response, @Nullable String fileName) {
        this.outStream = configureExcelFileName(request, response, null, fileName, false);
        this.excelWriter = EasyExcelFactory.write(this.outStream).excelType(ExcelTypeEnum.XLSX).build();
    }

    /**
     * [构造] 用于本地文件的工具
     *
     * @param fileDir  文件所在位置
     * @param fileName 文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     */
    public EasyExcelUtils(@Nullable String fileDir, @Nullable String fileName) {
        this.outStream = configureExcelFileName(null, null, fileDir, fileName, false);
        this.excelWriter = EasyExcelFactory.write(this.outStream).excelType(ExcelTypeEnum.XLSX).build();
    }

    /** [构造] 自定义输出流的工具 */
    public EasyExcelUtils(OutputStream outputStream) {
        this.outStream = outputStream;
        this.excelWriter = EasyExcelFactory.write(outStream).excelType(ExcelTypeEnum.XLSX).build();
    }

// ------------------------------ Static, 导入 ------------------------------

    /**
     * 导入 Excel 文件, 未通过校验的数据以 Excel 形式自动下载
     * <pre>
     * 1. 从第一行有效的 head 开始读取, 自动跳过在此之前无效的行
     * 2. 存在数据未通过校验时: 生成 Excel 并自动下载(仅包含未通过校验的数据) </pre>
     *
     * @param file       文件
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param excelClass Excel 类, 推荐 extends {@link EasyExcelClassTemplate}
     * @return 全部通过校验时--返回数据; 存在报错数据时--返回空白List, 并以 Excel 形式下载报错数据
     */
    @NonNull
    public static <T> List<T> importExcel(MultipartFile file, HttpServletRequest request, HttpServletResponse response, Class<T> excelClass) {
        ExcelListener<T> excelListener = new ExcelListener<>(excelClass);
        // 导入并进行初步校验: 成功 --> 返回数据; 失败 --> 自动下载报错信息
        if (Boolean.TRUE.equals(baseImportExcel(file, request, response, excelClass, excelListener, true, null))) {
            return excelListener.getValidList();
        }
        return new LinkedList<>();
    }

    /**
     * 导入 Excel 文件, 保存报错信息至 errorList
     * <pre>
     *   1. 从第一行有效的 head 开始读取, 自动跳过在此之前无效的行
     *   2. 导入完成后保存报错信息至 errorList
     * </pre>
     *
     * @param file       文件
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param excelClass excel 实体类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param errorList  存储报错信息
     * @return 返回通过校验的数据, 未通过校验的数据存储于 errorList 中
     */
    @NonNull
    public static <T> List<T> importExcelSaveError(@NonNull MultipartFile file, HttpServletRequest request, HttpServletResponse response,
                                                   Class<T> excelClass, @NonNull List<T> errorList) {
        ExcelListener<T> excelListener = new ExcelListener<>(excelClass);
        // 导入成功
        baseImportExcel(file, request, response, excelClass, excelListener, false, errorList);
        return excelListener.getValidList();
    }

    /**
     * [不指定 ExcelClass] 导入 Excel 文件
     * <pre>
     *   1. 从第一行有效的 head 开始读取, 自动跳过在此之前无效的行, 忽略未在 cnToEnHeadNameMap 中定义的 head
     *   2. 存在数据未通过校验时: 生成 Excel 并自动下载(仅包含未通过校验的数据)
     * </pre>
     *
     * @param file              文件
     * @param cnToEnHeadNameMap 中英列名对照, Map(中文, 英文)
     * @param headRules         列名校验规则
     * @return 全部通过校验时--返回数据; 存在报错数据时--返回空白List, 并以 Excel 形式下载报错数据
     */
    @NonNull
    public static List<Map<String, Object>> noModelImportExcel(@NonNull MultipartFile file, @Nullable Map<String, String> cnToEnHeadNameMap,
                                                               ExcelHeadRulesEnums headRules) {
        ExcelNoModelListener excelNoModelListener = new ExcelNoModelListener(cnToEnHeadNameMap, headRules);
        // 导入成功
        if (Boolean.TRUE.equals(noModelBaseImportExcel(file, excelNoModelListener, null))) {
            return excelNoModelListener.getValidList();
        }
        return new LinkedList<>();
    }

    /**
     * [不指定 ExcelClass] 导入 Excel 文件, 保存报错信息至 errorList
     * <pre>
     *   1. 从第一行有效的 head 开始读取, 自动跳过在此之前无效的行, 忽略未在 cnToEnHeadNameMap 中定义的 head
     *   2. 导入完成后保存报错信息至 errorList
     * </pre>
     *
     * @param file              文件
     * @param cnToEnHeadNameMap 中英列名对照, Map(中文, 英文)
     * @param errorList         存储报错信息
     * @param headRules         列名校验规则
     * @return 返回通过校验的数据, 未通过校验的数据存储于 errorList 中
     */
    @NonNull
    public static List<Map<String, Object>> noModelImportExcel(@NonNull MultipartFile file, @Nullable Map<String, String> cnToEnHeadNameMap,
                                                               List<List<Object>> errorList, ExcelHeadRulesEnums headRules) {
        ExcelNoModelListener excelNoModelListener = new ExcelNoModelListener(cnToEnHeadNameMap, headRules);
        // 导入成功
        if (Boolean.TRUE.equals(noModelBaseImportExcel(file, excelNoModelListener, errorList))) {
            return excelNoModelListener.getValidList();
        }
        return new LinkedList<>();
    }
// ============================== Static, 导入 End ==============================

// ------------------------------ Static, 导出 ------------------------------

    /**
     * [导出 Excel] http(s) 导出, 使用自定义配置
     *
     * @param request       HttpServletRequest
     * @param response      HttpServletResponse
     * @param excelClass    Excel 类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param excelDataList 数据, 格式需与 excelClass 保持一致
     * @param exportDto     Excel 导出参数
     */
    public static <T> void exportExcel(HttpServletRequest request, HttpServletResponse response,
                                       Class<T> excelClass, @Nullable List<T> excelDataList, EasyExcelExportDTO exportDto) {
        // 默认排除"错误信息"列
        exportDto.getExcludedCols().add(ExcelConstants.DEFAULT_ERROR_PARAM);

        baseExportExcel(request, response, null, excelClass, exportDto, excelDataList);
    }

    /**
     * [导出 Excel] 本地导出, 使用自定义配置
     *
     * @param fileDir       导出文件目录
     * @param excelClass    Excel 类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param excelDataList 数据, 格式需与 excelClass 保持一致
     * @param exportDto     Excel 导出参数
     */
    public static <T> void exportLocalExcel(@Nullable String fileDir, Class<T> excelClass, @Nullable List<T> excelDataList, EasyExcelExportDTO exportDto) {
        // 默认排除"错误信息"列
        exportDto.getExcludedCols().add(ExcelConstants.DEFAULT_ERROR_PARAM);

        baseExportExcel(null, null, fileDir, excelClass, exportDto, excelDataList);
    }

    /**
     * [导出 Excel] http(s) 导出数据
     *
     * @param request       HttpServletRequest
     * @param response      HttpServletResponse
     * @param fileName      文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param excelClass    Excel 类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param excelDataList 数据, 格式需与 excelClass 保持一致
     */
    public static <T> void exportData(HttpServletRequest request, HttpServletResponse response,
                                      @Nullable String fileName, Class<T> excelClass, @Nullable List<T> excelDataList) {
        // 配置参数
        EasyExcelExportDTO exportDto = new EasyExcelExportDTO();
        exportDto.setFileName(fileName);
        exportDto.setSheetName(ExcelConstants.DEFAULT_SHEET_NAME);
        // 默认排除"错误信息"列
        exportDto.getExcludedCols().add(ExcelConstants.DEFAULT_ERROR_PARAM);

        baseExportExcel(request, response, null, excelClass, exportDto, excelDataList);
    }

    /**
     * [导出 Excel] 本地导出数据
     *
     * @param fileDir       导出文件目录
     * @param fileName      文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param excelClass    Excel 类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param excelDataList 数据, 格式需与 excelClass 保持一致
     */
    public static <T> void exportLocalData(@Nullable String fileDir, @Nullable String fileName, Class<T> excelClass, @Nullable List<T> excelDataList) {
        // 配置参数
        EasyExcelExportDTO exportDto = new EasyExcelExportDTO();
        exportDto.setFileName(fileName);
        exportDto.setSheetName(ExcelConstants.DEFAULT_SHEET_NAME);
        // 默认排除"错误信息"列
        exportDto.getExcludedCols().add(ExcelConstants.DEFAULT_ERROR_PARAM);

        baseExportExcel(null, null, fileDir, excelClass, exportDto, excelDataList);
    }

    /**
     * 导出含有报错的 Excel(不再屏蔽"错误信息"列)
     *
     * @param request       HttpServletRequest
     * @param response      HttpServletResponse
     * @param excelClass    Excel 类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param excelDataList 数据, 格式需与 excelClass 保持一致
     * @param exportDto     Excel 导出参数
     */
    public static <T> void exportErrorExcel(HttpServletRequest request, HttpServletResponse response,
                                            Class<T> excelClass, @Nullable List<T> excelDataList, EasyExcelExportDTO exportDto) {
        baseExportExcel(request, response, null, excelClass, exportDto, excelDataList);
    }

    /**
     * 导出 Excel 空白模板: 不包含任何数据, 可添加说明
     *
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param fileName   文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param excelClass Excel 类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param note       需要添加的填表说明, 位于列名之上
     */
    public static void exportTemplate(HttpServletRequest request, HttpServletResponse response,
                                      @Nullable String fileName, Class<?> excelClass, @Nullable String note) {
        // 配置参数
        EasyExcelExportDTO exportDto = new EasyExcelExportDTO();
        exportDto.setFileName(fileName);
        exportDto.setSheetName(ExcelConstants.DEFAULT_SHEET_NAME);
        exportDto.setNote(note);
        exportDto.setWidthStrategy(ExcelColWidthStrategy.COL_WIDTH_HEAD);
        // 默认排除"错误信息"列
        exportDto.getExcludedCols().add(ExcelConstants.DEFAULT_ERROR_PARAM);

        baseExportExcel(request, response, null, excelClass, exportDto, null);
    }

    /**
     * [不指定 ExcelClass]  http(s) 导出, 使用自定义配置
     *
     * @param request   http servlet request
     * @param response  http servlet response
     * @param exportDto Excel 导出参数
     */
    public static void noModelExportExcel(HttpServletRequest request, HttpServletResponse response, EasyExcelNoModelExportDTO exportDto) {
        noModelBaseExportExcel(request, response, null, exportDto);
    }

    /**
     * [不指定 ExcelClass] 本地导出, 使用自定义配置
     *
     * @param fileDir   导出文件目录
     * @param exportDto Excel 导出参数
     */
    public static void noModelExportLocalExcel(String fileDir, EasyExcelNoModelExportDTO exportDto) {
        noModelBaseExportExcel(null, null, fileDir, exportDto);
    }

    /**
     * [不指定 ExcelClass] http(s) 导出数据, 使用自定义配置
     *
     * @param request       http servlet request
     * @param response      http servlet response
     * @param fileName      文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param headList      中文列名, 单行列名: List(string); 多行复合列名(相同标题自动合并): List(List(string));
     *                      例: List(List(第一行, 第二行1),List(第一行, 第二行2)),其中"第一行"会自动合并
     * @param enToCnHeadMap 列名对照, Map(英文, 中文)
     * @param dataList      表内数据
     */
    public static void noModelExportData(HttpServletRequest request, HttpServletResponse response, @Nullable String fileName,
                                         List<String> headList, @Nullable Map<String, String> enToCnHeadMap, @Nullable List<Map<String, Object>> dataList) {
        // 配置参数
        EasyExcelNoModelExportDTO exportDto = new EasyExcelNoModelExportDTO();
        exportDto.setFileName(fileName);
        exportDto.setEnSimpleHeadList(headList);
        if (enToCnHeadMap != null) {
            exportDto.setEnToCnHeadMap(enToCnHeadMap);
        }
        if (dataList != null) {
            exportDto.setDataList(dataList);
        }

        noModelBaseExportExcel(request, response, null, exportDto);
    }

    /**
     * [不指定 ExcelClass] 本地导出数据, 使用自定义配置
     *
     * @param fileDir       导出文件目录
     * @param fileName      文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param headList      中文列名, 单行列名: List(string); 多行复合列名(相同标题自动合并): List(List(string));
     *                      例: List(List(第一行, 第二行1),List(第一行, 第二行2)),其中"第一行"会自动合并
     * @param enToCnHeadMap 列名对照, Map(英文, 中文)
     * @param dataList      表内数据
     */
    public static void noModelExportLocalData(String fileDir, @Nullable String fileName,
                                              List<String> headList, @Nullable Map<String, String> enToCnHeadMap, @Nullable List<Map<String, Object>> dataList) {
        // 配置参数
        EasyExcelNoModelExportDTO exportDto = new EasyExcelNoModelExportDTO();
        exportDto.setFileName(fileName);
        exportDto.setEnSimpleHeadList(headList);
        if (dataList != null) {
            exportDto.setDataList(dataList);
        }
        if (enToCnHeadMap != null) {
            exportDto.setEnToCnHeadMap(enToCnHeadMap);
        }

        noModelBaseExportExcel(null, null, fileDir, exportDto);
    }

    /**
     * [不指定 ExcelClass] 导出 Excel 模板: 数据为空, 可添加说明
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param fileName 文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param headList 单行列名
     * @param note     需要添加的说明, 位于列名之上
     */
    public static void noModelExportTemplate(HttpServletRequest request, HttpServletResponse response,
                                             @Nullable String fileName, List<String> headList, @Nullable String note) {
        // 配置参数
        EasyExcelNoModelExportDTO exportDto = new EasyExcelNoModelExportDTO();
        exportDto.setFileName(fileName);
        exportDto.setEnSimpleHeadList(headList);
        exportDto.setNote(note);

        noModelBaseExportExcel(request, response, null, exportDto);
    }
// ============================== Static, 导出 End ==============================

// ------------------------------ Non-static ------------------------------

    /**
     * 分页导出 Excel: 写入新的 sheet, 全部导出完成后必须手动调用 {@link #closeExcel} 关闭流
     *
     * @param excelClass excel 实体类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param dataList   表内数据
     * @param exportDto  Excel 导出参数
     */
    public void writeSheet(Class<?> excelClass, List<?> dataList, EasyExcelExportDTO exportDto) {
        // 默认排除"错误信息"列
        exportDto.getExcludedCols().add(ExcelConstants.DEFAULT_ERROR_PARAM);
        baseWriteSheet(excelWriter, excelClass, sheetIndex, exportDto, dataList);
        sheetIndex++;
    }

    /** [不指定 ExcelClass] 分页导出 Excel: 写入新的 sheet, 全部导出完成后必须手动调用 {@link #closeExcel} 关闭流 */
    public void noModelWriteSheet(EasyExcelNoModelExportDTO exportDto) {
        noModelBaseWriteSheet(excelWriter, sheetIndex, exportDto);
        sheetIndex++;
    }

    /** 关闭所有的流 */
    public void closeExcel() {
        if (excelWriter != null) {
            excelWriter.finish();
        }
        try {
            if (outStream != null) {
                outStream.close();
            }
        } catch (IOException e) {
            log.error("Excel 关闭输出流失败", e);
            throw new IllegalStateException("Excel 关闭输出流失败");
        }
    }

// ------------------------------ Private ------------------------------

    /**
     * 导入 Excel 文件, 使用自定义的 ExcelListener 配置
     *
     * @param file        文件
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     * @param excelClass  Excel 类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param listener    ExcelListener, 允许 extends {@link ExcelListener}
     * @param exportError 是否自动导出报错
     * @param errorList   不自动导出时用于保存报错(exportError == false)
     * @return true = 全部数据通过校验, false = 文件为空或存在数据未通过校验
     */
    @Nullable
    private static <T, U extends ExcelListener<T>> Boolean baseImportExcel(@NonNull MultipartFile file, HttpServletRequest request, HttpServletResponse response, Class<T> excelClass, U listener, Boolean exportError, List<T> errorList) {
        try {
            // 读取数据
            EasyExcelFactory.read(file.getInputStream(), excelClass, listener).extraRead(CellExtraTypeEnum.MERGE).sheet().doRead();
            // head 无效时跳过前 x 行
            while (Boolean.FALSE.equals(listener.getValidHead())) {
                EasyExcelFactory.read(file.getInputStream(), excelClass, listener).extraRead(CellExtraTypeEnum.MERGE).headRowNumber(listener.getHeadRowNum()).sheet().doRead();
            }

            // 报错处理
            String fileName = StringUtils.isBlank(file.getOriginalFilename()) ? "" : file.getOriginalFilename();
            String fileNameNoPostfix = fileName.lastIndexOf('.') > 0 ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
            String errorFileName = fileNameNoPostfix + " Excel 导入报错" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            // 存在错误信息
            if (!listener.getInvalidList().isEmpty() && request != null && response != null) {
                if (Boolean.TRUE.equals(exportError)) {
                    // 导出包含报错的 Excel
                    EasyExcelExportDTO exportDto = new EasyExcelExportDTO();
                    exportDto.setFileName(errorFileName);
                    baseExportExcel(request, response, null, excelClass, exportDto, listener.getInvalidList());
                    return null;
                } else {
                    // 仅保存报错信息
                    if (errorList != null) {
                        errorList.addAll(listener.getInvalidList());
                    }
                }
            }
        } catch (IOException e) {
            log.error("{} Excel 导入异常, 请检查导入文件或 Excel 类 {}", file.getOriginalFilename(), excelClass.getName(), e);
            return null;
        }
        return true;
    }

    /**
     * 不指定 ExcelClass 导入 Excel 文件, 使用自定义的 ExcelListener 配置
     *
     * @param file      文件
     * @param errorList 保存报错数据
     * @return true = 成功, false = 文件为空
     */
    @Nullable
    private static <T extends ExcelNoModelListener> Boolean noModelBaseImportExcel(MultipartFile file, T listener, @Nullable List<List<Object>> errorList) {
        if (file == null) {
            log.error("上传的 Excel 文件为空");
            return false;
        }
        try {
            // 读取数据
            EasyExcelFactory.read(file.getInputStream(), listener).extraRead(CellExtraTypeEnum.MERGE).sheet().doRead();
            // head 无效时跳过前 x 行
            while (Boolean.FALSE.equals(listener.getValidHead())) {
                EasyExcelFactory.read(file.getInputStream(), listener).extraRead(CellExtraTypeEnum.MERGE).headRowNumber(listener.getHeadRowNum()).sheet().doRead();
            }

            // 记录报错信息
            if (!listener.getInvalidList().isEmpty() && (errorList != null)) {
                errorList.addAll(listener.getInvalidList());
            }
        } catch (IOException e) {
            log.error("{} Excel 导入异常, 请检查导入文件 ", file.getOriginalFilename(), e);
            return null;
        }
        return true;
    }

    /**
     * [不指定 ExcelClass] 导出 Excel
     *
     * @param request   http servlet request
     * @param response  http servlet response
     * @param fileDir   导出文件目录
     * @param exportDto Excel 导出参数
     */
    private static void noModelBaseExportExcel(HttpServletRequest request, HttpServletResponse response, String fileDir, EasyExcelNoModelExportDTO exportDto) {
        // 创建 excel writer
        String fileName = exportDto.getFileName();
        OutputStream outputStream = configureExcelFileName(request, response, fileDir, fileName, exportDto.getUseExcel07());
        ExcelWriter excelWriter;
        excelWriter = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();

        // 写入数据
        noModelBaseWriteSheet(excelWriter, 1, exportDto);

        // 关闭 excel writer
        closeExcelWriter(outputStream, excelWriter);
    }

    /**
     * Excel 导出: 写入单张 Sheet(导出完成后需手动调用 {@link #closeExcelWriter} 关闭流)
     *
     * @param excelWriter excel 主体
     * @param excelClass  待转换的实体类
     * @param sheetIndex  表序号
     * @param exportDto   Excel 导出参数
     * @param dataList    表内数据
     */
    private static void baseWriteSheet(ExcelWriter excelWriter, Class<?> excelClass, Integer sheetIndex, EasyExcelExportDTO exportDto, @Nullable List<?> dataList) {
        // 动态下拉框
        Map<String, String[]> dynamicMenuMap = exportDto.getDynamicMenuMap();
        Map<Integer, String[]> indexedDynamicMenuMap = new HashMap<>();
        // 记录多选框的列序号
        Set<Integer> indexMultipleDropDownSet = new HashSet<>();
        // 联动下拉框
        Map<String, List<Integer>> cascadeMenuIndexMap = new HashMap<>();
        // 统一配置下拉框
        configureDropDownMenus(excelClass, exportDto, dynamicMenuMap, indexedDynamicMenuMap, cascadeMenuIndexMap, indexMultipleDropDownSet);

        // 联动下拉框后续处理
        Map<String, List<ExcelCascadeOption>> cascadeMenuMap = exportDto.getCascadeMenuMap();
        // 仅存在一组联动下拉框, 进行默认配置
        if (cascadeMenuMap.isEmpty()) {
            if (cascadeMenuIndexMap.size() == 1) {
                String cascadeGroupName = cascadeMenuIndexMap.keySet().iterator().next();
                cascadeMenuMap.put(cascadeGroupName, exportDto.getCascadeMenu());
            } else if (cascadeMenuIndexMap.size() > 1) {
                throw new UnsupportedOperationException("存在多组联动下拉框, 请配置组名");
            }
        }

        // 排除指定的列, 自动忽略未使用 ExcelProperty 进行注解的列
        int validColumnNum = 0;  // 计数, 未被排除的列
        Set<String> excludedCols = exportDto.getExcludedCols();
        List<String[]> originalHeadList = new ArrayList<>();  // 记录列名, 用于动态列名替换
        Set<String> doNotChangeWidth = new HashSet<>();       // 记录手动指定列宽的字段
        validColumnNum = configureValidCols(excelClass, excludedCols, originalHeadList, validColumnNum, doNotChangeWidth);

        // 动态列名, 对旧列名进行替换
        List<List<String>> newHeadList = new ArrayList<>();
        Map<String, String> replaceHeadMap = exportDto.getReplaceHeadMap();
        boolean isHeadReplaced = replaceHead(replaceHeadMap, originalHeadList, newHeadList);

        // 创建表单
        String sheetName = exportDto.getSheetName();
        String finalSheetName = StringUtils.isBlank(sheetName) ? "sheet" + sheetIndex : sheetName;
        ExcelWriterSheetBuilder sheetBuilder = EasyExcelFactory.writerSheet(sheetIndex, finalSheetName);

        // 需要排除的列
        sheetBuilder.excludeColumnFieldNames(excludedCols);

        // 调整下拉框位置
        int skipRowNum = 0;
        String title = exportDto.getTitle();
        if (StringUtils.isNotBlank(title)) {
            skipRowNum++;
        }
        String note = exportDto.getNote();
        if (StringUtils.isNotBlank(note)) {
            skipRowNum++;
        }

        // 表单设置: 列宽, 行高, 下拉框...
        ExcelColWidthStrategy widthStrategy = exportDto.getWidthStrategy();
        sheetBuilder.registerWriteHandler(new ExcelSheetWriteHandler(skipRowNum, indexedDynamicMenuMap,
                cascadeMenuIndexMap, cascadeMenuMap, indexMultipleDropDownSet));
        sheetBuilder.registerWriteHandler(new ExcelColumnWidthHandler(widthStrategy, doNotChangeWidth));
        if (Boolean.TRUE.equals(exportDto.getAutoRowHeight())) {
            sheetBuilder.registerWriteHandler(new ExcelRowHeightHandler());
        }

        // 单表(不使用标题与自定义说明)且需要动态列名时, 需要在 writer 中应用 head 样式, 并且提前终止
        boolean needEarlyStop = isHeadReplaced && StringUtils.isBlank(title) && StringUtils.isBlank(note);
        if (needEarlyStop) {
            sheetBuilder.head(excelClass).head(newHeadList).build();
            excelWriter.write(dataList, sheetBuilder.build());
            return;
        }

        // 配置导出设置
        int mainTableIndex = 0;
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
        // 主表
        WriteTable mainTable;
        if (isHeadReplaced) {
            // 动态列名
            mainTable = EasyExcelFactory.writerTable(mainTableIndex).head(excelClass).head(newHeadList).needHead(true).build();
        } else {
            // 常规列名
            mainTable = EasyExcelFactory.writerTable(mainTableIndex).head(excelClass).needHead(true).build();
        }
        excelWriter.write(dataList, sheetBuilder.build(), mainTable);
    }

    /**
     * [不指定 ExcelClass] Excel 导出: 写入单张 Sheet(导出完成后需手动调用 {@link #closeExcelWriter} 关闭流)
     *
     * @param excelWriter Excel 导出主体
     * @param sheetIndex  [允许 null] 表序号
     * @param exportDto   excel 参数
     */
    private static void noModelBaseWriteSheet(ExcelWriter excelWriter, Integer sheetIndex, EasyExcelNoModelExportDTO exportDto) {
        // 创建表单
        String sheetName = exportDto.getSheetName();
        String finalSheetName = StringUtils.isBlank(sheetName) ? "sheet" + sheetIndex : sheetName;
        ExcelWriterSheetBuilder sheetBuilder = EasyExcelFactory.writerSheet(sheetIndex, finalSheetName);

        // 配置列名, 联动下拉框, 数据
        List<List<String>> cnHeadList = new LinkedList<>();
        List<List<Object>> finalDataList = new LinkedList<>();
        List<String> orderedEnHead = new LinkedList<>();
        setHeadAndDataNoModel(exportDto, cnHeadList, finalDataList, orderedEnHead);

        // 联动下拉框, 根据名称匹配下拉框所在列的序号
        Map<String, Integer> colIndexMap = new HashMap<>();
        for (int i = 0; i < orderedEnHead.size(); i++) {
            colIndexMap.put(orderedEnHead.get(i), i);
        }
        Map<String, List<Integer>> cascadeMenuIndexMap = new HashMap<>();
        for (Map.Entry<String, List<String>> cascadeGroup : exportDto.getCascadeColMap().entrySet()) {
            List<Integer> indexList = new LinkedList<>();
            cascadeGroup.getValue().forEach(colName -> indexList.add(colIndexMap.get(colName)));
            cascadeMenuIndexMap.put(cascadeGroup.getKey(), indexList);
        }

        // 调整下拉框位置
        Map<Integer, String[]> indexedDropDownMap = configureIndexedDropDownMap(exportDto.getDropDownMap(), orderedEnHead);
        int skipRowNum = 0;
        if (StringUtils.isNotBlank(exportDto.getTitle())) {
            skipRowNum++;
        }
        if (StringUtils.isNotBlank(exportDto.getNote())) {
            skipRowNum++;
        }
        int headRows = 0;
        for (List<String> headColumn : cnHeadList) {
            headRows = Math.max(headRows, headColumn.size());
        }
        skipRowNum = skipRowNum + headRows - 1;

        // 特殊标注的列名英文转中文, 无对应中文的保持英文
        Set<String> cnSpecialHeadSet = new HashSet<>();
        for (String enSpecialHead : exportDto.getImportantHeadSet()) {
            cnSpecialHeadSet.add(exportDto.getEnToCnHeadMap().getOrDefault(enSpecialHead, enSpecialHead));
        }

        // 表单设置: 列宽, 行高, 下拉框...
        sheetBuilder.registerWriteHandler(new ExcelSheetWriteHandler(skipRowNum, indexedDropDownMap, cascadeMenuIndexMap, exportDto.getCascadeMenuMap()));
        sheetBuilder.registerWriteHandler(new ExcelNoModelVerticalStyleHandler(cnSpecialHeadSet));
        sheetBuilder.registerWriteHandler(new ExcelColumnWidthHandler(exportDto.getWidthStrategy()));
        if (Boolean.TRUE.equals(exportDto.getAutoRowHeight())) {
            sheetBuilder.registerWriteHandler(new ExcelRowHeightHandler());
        }

        // 根据是否存在标题与自定义说明, 配置导出设置
        int mainTableIndex = 0;
        int validColumnNum = cnHeadList.size();
        // 单表(不使用标题与自定义说明)且需要动态列名时, 需要在 writer 中应用 head 样式
        // 标题
        if (StringUtils.isNotBlank(exportDto.getTitle())) {
            writeTitle(excelWriter, sheetBuilder.build(), EasyExcelClassTemplate.class, exportDto.getTitle(), validColumnNum, mainTableIndex);
            // tableIndex 顺沿
            mainTableIndex++;
        }
        // 自定义说明
        if (StringUtils.isNotBlank(exportDto.getNote())) {
            writeNote(excelWriter, sheetBuilder.build(), EasyExcelClassTemplate.class, exportDto.getNote(), validColumnNum, mainTableIndex);
            // tableIndex 顺沿
            mainTableIndex++;
        }

        // 导出
        WriteTable mainTable;
        mainTable = EasyExcelFactory.writerTable(mainTableIndex).head(cnHeadList).needHead(true).build();
        excelWriter.write(finalDataList, sheetBuilder.build(), mainTable);
    }

    /**
     * 导出 Excel
     *
     * @param request    http servlet request
     * @param response   http servlet response
     * @param fileDir    导出文件目录
     * @param excelClass 待转换的实体类
     * @param exportDto  Excel 导出参数
     * @param dataList   表内数据, 不填充传 null 即可
     */
    private static void baseExportExcel(HttpServletRequest request, HttpServletResponse response, String fileDir, Class<?> excelClass,
                                        EasyExcelExportDTO exportDto, @Nullable List<?> dataList) {
        // 创建 excel writer
        ExcelWriter excelWriter;
        String fileName = exportDto.getFileName();
        OutputStream outputStream = configureExcelFileName(request, response, fileDir, fileName, exportDto.getUseExcel07());
        excelWriter = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();

        // 写入数据
        baseWriteSheet(excelWriter, excelClass, 1, exportDto, dataList);

        // 关闭 excel writer
        closeExcelWriter(outputStream, excelWriter);
    }

    /**
     * 动态列名, 对旧列名进行替换
     *
     * @param replaceHeadMap   列名替换, map(旧列名, 新列名)
     * @param originalHeadList 替换前的 head
     * @param newHeadList      替换后的 head
     * @return true--已替换至少一个列名; false--未替换列名
     */
    private static boolean replaceHead(Map<String, String> replaceHeadMap, List<String[]> originalHeadList, List<List<String>> newHeadList) {
        boolean headReplaced = false;
        if (!replaceHeadMap.isEmpty()) {
            for (String[] headArray : originalHeadList) {
                // 检索旧列名, 并进行替换
                for (int i = 0; i < headArray.length; i++) {
                    String newHead = replaceHeadMap.get(headArray[i]);
                    if (StringUtils.isNotBlank(newHead)) {
                        headArray[i] = newHead;
                        headReplaced = true;
                    }
                }
                newHeadList.add(new ArrayList<>(Arrays.asList(headArray)));
            }
        }
        return headReplaced;
    }

    /**
     * 初步配置 excel 列(排除指定的列, 自动忽略未使用 ExcelProperty 进行注解的列)
     *
     * @return 有效列的数量
     */
    private static int configureValidCols(Class<?> excelClass, Set<String> excludedCols, List<String[]> originalHeadList, int validColumnNum, Set<String> doNotChangeWidth) {
        for (Field field : excelClass.getDeclaredFields()) {
            // 仅记录 ExcelProperty 注解的列
            ExcelProperty excelAnnotation = field.getAnnotation(ExcelProperty.class);
            if (excelAnnotation != null) {
                // 跳过被排除的列
                if (!excludedCols.contains(field.getName())) {
                    // 记录列名, 用于动态列名替换
                    originalHeadList.add(excelAnnotation.value());
                    // 统计有效列的总数
                    validColumnNum++;
                    // 记录手动指定的列宽
                    if (excelClass.getAnnotation(ColumnWidth.class) != null || field.getAnnotation(ColumnWidth.class) != null) {
                        doNotChangeWidth.add(field.getName());
                    }
                }
            } else {
                // 自动忽略未使用 ExcelProperty 进行注解的列
                excludedCols.add(field.getName());
            }
        }
        return validColumnNum;
    }

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
        WriteTable noteTable = EasyExcelFactory.writerTable(mainTableIndex)
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
        ExcelWriterTableBuilder tableBuilder = EasyExcelFactory.writerTable(mainTableIndex).head(targetClass).registerWriteHandler(noteStrategy).needHead(false);
        // 合并首行
        if (validColumnNum > 1) {
            OnceAbsoluteMergeStrategy mergeNoteRow = new OnceAbsoluteMergeStrategy(mainTableIndex, mainTableIndex, 0, validColumnNum - 1);
            tableBuilder.registerWriteHandler(mergeNoteRow);
        }
        WriteTable noteTable = tableBuilder.build();
        excelWriter.write(noteContent, writeSheet, noteTable);
    }

    /**
     * 配置下拉框, 处理 {@link ExcelDropDown} 注解
     *
     * @param targetClass              Excel 实体类
     * @param exportDto                Excel 导出参数
     * @param dynamicMenuMap           动态下拉框, Map(列名, 选项); 其中列名必须使用 {@link ExcelDropDown#dynamicMenuName} 在 ExcelClass 进行定义
     * @param indexedDynamicMenuMap    记录动态下拉框在 Excel 中的位置, Map(列, 选项)
     * @param cascadeMenuIndexMap      记录联动下拉框在 Excel 中的位置, Map(组名, 列)
     * @param indexMultipleDropDownSet 记录多选框的列序号
     */
    private static void configureDropDownMenus(Class<?> targetClass, EasyExcelExportDTO exportDto,
                                               @NonNull Map<String, String[]> dynamicMenuMap, @NonNull Map<Integer, String[]> indexedDynamicMenuMap,
                                               @NonNull Map<String, List<Integer>> cascadeMenuIndexMap, @NonNull Set<Integer> indexMultipleDropDownSet) {

        // 遍历 Class 所有字段
        Field[] fieldArray = targetClass.getDeclaredFields();
        // index 根据排除的列进行调整
        Set<String> excludedCols = exportDto.getExcludedCols();
        int indexLeftShift = 0;
        // 查找有下拉框注解的字段, 填充相关数据
        for (int index = 0; index < fieldArray.length; index++) {
            Field field = fieldArray[index];
            // 排除的列, 跳过的同时后续 index 均需要调整
            if (excludedCols.contains(field.getName())) {
                indexLeftShift++;
                continue;
            }
            configureExcelDropDown(dynamicMenuMap, indexedDynamicMenuMap, cascadeMenuIndexMap, indexMultipleDropDownSet, field, index, indexLeftShift);
        }
    }

    /**
     * [不指定 ExcelClass] 整理列名与数据格式
     *
     * @param exportDto     excel 参数
     * @param cnHeadList    整理后的中文列名
     * @param finalDataList 整理后的数据
     * @param orderedEnHead 整理后的最底层英文列名
     */
    private static void setHeadAndDataNoModel(EasyExcelNoModelExportDTO exportDto, List<List<String>> cnHeadList,
                                              List<List<Object>> finalDataList, List<String> orderedEnHead) {
        // 不允许列名为空
        if (exportDto.getEnSimpleHeadList().isEmpty() && exportDto.getEnHeadList().isEmpty()) {
            log.error("Excel 列名为空");
            return;
        }

        // 整理列名
        orderedEnHead = configureEnglishHead(exportDto, cnHeadList, orderedEnHead);

        // 根据列名顺序整理数据
        List<Map<String, Object>> srcDataList = exportDto.getDataList();
        if (!srcDataList.isEmpty()) {
            for (Map<String, Object> currDataMap : srcDataList) {
                List<Object> innerDataList = new LinkedList<>();
                for (String englishHeadName : orderedEnHead) {
                    innerDataList.add(currDataMap.get(englishHeadName));
                }
                finalDataList.add(innerDataList);
            }
        }
    }

    /**
     * [不指定 ExcelClass] 整理列名
     *
     * @param exportDto     excel 参数
     * @param cnHeadList    整理后的中文列名
     * @param orderedEnHead 整理后的最底层英文列名
     */
    private static List<String> configureEnglishHead(EasyExcelNoModelExportDTO exportDto,
                                                     @NonNull List<List<String>> cnHeadList, @NonNull List<String> orderedEnHead) {
        // 获取列名对照
        Map<String, String> enToCnHeadMap = exportDto.getEnToCnHeadMap();

        if (!exportDto.getEnHeadList().isEmpty()) {
            // 优先处理多行列名
            for (List<String> columnHeadList : exportDto.getEnHeadList()) {
                List<String> newColumnHeadList = new LinkedList<>();
                String bottomEnglishHead = "";
                for (String currHead : columnHeadList) {
                    // 有中文转中文, 无中文保持英文
                    String headToAdd = enToCnHeadMap.getOrDefault(currHead, currHead);
                    newColumnHeadList.add(headToAdd);
                    bottomEnglishHead = currHead;
                }
                // 记录中文列名
                cnHeadList.add(newColumnHeadList);
                // 记录最底层列名, 作为数据顺序依据
                orderedEnHead.add(bottomEnglishHead);
            }
        } else if (!exportDto.getEnSimpleHeadList().isEmpty()) {
            // 无多行列名时使用单行列名
            orderedEnHead = exportDto.getEnSimpleHeadList();
            // 单行列名
            for (String currHead : orderedEnHead) {
                List<String> columnHeadList = new LinkedList<>();
                // 有中文转中文, 无中文保持英文
                String headToAdd = enToCnHeadMap.getOrDefault(currHead, currHead);
                columnHeadList.add(headToAdd);
                cnHeadList.add(columnHeadList);
            }
        }
        return orderedEnHead;
    }

    /**
     * [不指定 ExcelClass] 配置下拉框
     *
     * @param dropDownMap        下拉框, Map(英文列名, 选项)
     * @param orderedEnglishHead 最底层英文列名
     */
    private static Map<Integer, String[]> configureIndexedDropDownMap(@NonNull Map<String, String[]> dropDownMap, @NonNull List<String> orderedEnglishHead) {
        Map<Integer, String[]> indexedDropDownMap = new HashMap<>();
        int index = 0;
        for (String head : orderedEnglishHead) {
            if (dropDownMap.containsKey(head)) {
                indexedDropDownMap.put(index, dropDownMap.get(head));
            }
            index++;
        }
        return indexedDropDownMap;
    }

    /**
     * 处理 {@link ExcelDropDown} 注解
     *
     * @param dynamicMenuMap           动态下拉框, Map(列名, 选项); 其中列名必须使用 {@link ExcelDropDown#dynamicMenuName} 在 ExcelClass 进行定义
     * @param indexedDynamicMenuMap    记录动态下拉框在 Excel 中的位置, Map(列, 选项)
     * @param cascadeMenuIndexMap      记录联动下拉框在 Excel 中的位置, Map(组名, 列)
     * @param indexMultipleDropDownSet 记录多选框的列序号
     * @param field                    当前处理的 ExcelClass 字段
     * @param index                    当前 Excel 列序号
     * @param indexLeftShift           跳过的列数量(部分列会被 {@link EasyExcelExportDTO#getExcludedCols()} 排除)
     */

    private static void configureExcelDropDown(Map<String, String[]> dynamicMenuMap, Map<Integer, String[]> indexedDynamicMenuMap,
                                               Map<String, List<Integer>> cascadeMenuIndexMap, Set<Integer> indexMultipleDropDownSet,
                                               Field field, int index, int indexLeftShift) {
        // 检测到 @ExcelDropDown 注解, 进行下拉框配置
        ExcelDropDown excelDropDown = field.getAnnotation(ExcelDropDown.class);
        if (excelDropDown != null) {
            // 使用排除指定列后的 index
            int shiftedIndex = index - indexLeftShift;
            String[] dynamicOptions = excelDropDown.value();

            // 动态下拉框配置
            String dynamicMenuName = excelDropDown.dynamicMenuName();
            boolean hasDynamicOptions = !StringUtils.isEmpty(dynamicMenuName) && (dynamicMenuMap.get(dynamicMenuName) != null && dynamicMenuMap.get(dynamicMenuName).length > 0);
            if (hasDynamicOptions) {
                dynamicOptions = dynamicMenuMap.get(dynamicMenuName);
            }
            // 仅记录有效的下拉框
            if (dynamicOptions != null && dynamicOptions.length > 0) {
                indexedDynamicMenuMap.put(shiftedIndex, dynamicOptions);
            }

            // 联动下拉框配置
            String cascadeGroupName = excelDropDown.cascadeGroupName();
            if (StringUtils.isNotBlank(cascadeGroupName)) {
                cascadeMenuIndexMap.computeIfAbsent(cascadeGroupName, k -> new LinkedList<>()).add(shiftedIndex);
            }

            // 多选下拉框配置
            if (excelDropDown.multiple()) {
                indexMultipleDropDownSet.add(shiftedIndex);
            }
        }
    }

    /** 配置 Excel 文件名 */
    private static OutputStream configureExcelFileName(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response,
                                                       @Nullable String fileDir, @Nullable String fileName, @Nullable Boolean useExcel07) {
        // 设置文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
        fileName = configureFileName(fileName);

        // 配置文件路径
        String filePath = configureFilePath(fileDir, fileName);

        // http(s) 操作, 处理中文乱码
        if (request != null && response != null) {
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE") || userAgent.contains("Trident") || userAgent.contains("Chrome")) {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            } else {
                fileName = new String(fileName.getBytes(), StandardCharsets.ISO_8859_1);
            }
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);

            // 设置文件格式, 编码
            if (useExcel07 == Boolean.TRUE) {
                // 07 版 Excel, 可以解决部分 Excel 不兼容问题
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            } else {
                // 03 版 Excel
                response.setContentType("application/vnd.ms-excel");
            }
            response.setCharacterEncoding("utf-8");
            try {
                return response.getOutputStream();
            } catch (IOException e) {
                log.error("获取 http(s) 输出流失败, 文件名: {}", fileName, e);
                throw new UnsupportedOperationException("获取 http(s) 输出流失败, 文件名: " + fileName);
            }
        } else {
            // 本地文件操作
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(filePath);
                return fileOutputStream;
            } catch (IOException e) {
                log.error("创建 Excel 文件失败, 请检查路径: {}", filePath, e);
                throw new IllegalArgumentException("创建 Excel 文件失败, 请检查路径: " + filePath, e);
            }
        }
    }

    /** 设置文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx" */
    private static String configureFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            fileName = "Excel 导出数据" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
        } else if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            fileName = fileName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
        }
        return fileName;
    }

    /** 配置文件路径 */
    private static String configureFilePath(String fileDir, String fileName) {
        String filePath;
        if (StringUtils.isNotBlank(fileDir)) {
            if (fileDir.endsWith(File.separator)) {
                filePath = fileDir + fileName;
            } else {
                filePath = fileDir + File.separator + fileName;
            }
        } else {
            filePath = fileName;
        }
        return filePath;
    }

    /** 关闭 Excel Writer 以及输出流 */
    private static void closeExcelWriter(OutputStream outputStream, ExcelWriter excelWriter) {
        // 关闭 excel writer
        if (excelWriter != null) {
            excelWriter.finish();
        }
        // 关闭输出流
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            log.error("关闭输出流失败", e);
            throw new IllegalStateException("关闭输出流失败", e);
        }
    }
}