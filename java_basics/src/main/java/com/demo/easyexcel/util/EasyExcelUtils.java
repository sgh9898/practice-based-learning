package com.demo.easyexcel.util;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.demo.easyexcel.util.annotation.ExcelDropDown;
import com.demo.easyexcel.util.constants.EasyExcelConstants;
import com.demo.easyexcel.util.enums.ExcelColWidthEnums;
import com.demo.easyexcel.util.listner.EasyExcelTemplateListener;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateEntity;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateExcelVo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;

/**
 * EasyExcel 工具类
 * <br> 0. 准备: -- Excel 类需要 extends {@link EasyExcelTemplateExcelVo}, 内有详细配置说明
 * <br>         -- Excel 对应的 Entity 需要 extends {@link EasyExcelTemplateEntity}
 * <br> 1. 导入: 1) 返回 Excel: {@link #importAsExcel}
 * <br>         2) 返回 Entity: {@link #importAsEntity}
 * <br>         3) 自定义 {@link EasyExcelTemplateListener}, 然后从 Listener 读取: {@link #importExcel}
 * <br> 2. 导出: 1) 简单导出 Excel: {@link #exportData}
 * <br>         2) 空白模板: {@link #exportTemplate}
 * <br>         3) 进行自定义配置后导出: {@link #downloadCustomizedExcel}
 * <br> 3. 自定义配置: 1) 导出时排除指定列: {@link #excludedCols}
 * <br>              2) 动态列名: {@link #headMap}
 * <br>              3) 动态下拉框: {@link #dynamicDropDownMap}, 静态下拉框可以直接使用 {@link ExcelDropDown} 在 Excel 类上进行配置
 * <br>              4) 在列名之上添加说明行: {@link #setNote}
 * <br>              5) 在说明行之上添加标题行: {@link #setTitle}
 *
 * @author Song gh on 2023/3/1.
 */
@Slf4j
@Setter
public class EasyExcelUtils {

    // ------------------------------ 常量 ------------------------------
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
    /** Excel 实体类名 */
    private Class<? extends EasyExcelTemplateExcelVo> excelClass;
    /** 导出数据 */
    private List<? extends EasyExcelTemplateExcelVo> dataList;

    // ------------------------------ 变量 ------------------------------
    /** 导出时排除的列名(Excel 类中字段英文原名) */
    private Set<String> excludedCols;
    /** 动态列名, 格式: Map(原名, 新名), 原名必须使用 {@link ExcelProperty#value} 配置 */
    private Map<String, String> headMap;
    /** 动态下拉框, 格式: Map(名称, 选项), 名称必须使用 {@link ExcelDropDown#name} 配置在 Excel 类中) */
    private Map<String, String[]> dynamicDropDownMap;

    /** 列宽选取方式 */
    private ExcelColWidthEnums widthStrategy;
    /** 启用 07 版 Excel: 默认 false, 出现兼容问题时可尝试 true */
    private Boolean useExcel07;

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

    // ------------------------------ 构造 ------------------------------

    /** 构造: 表名, Class */
    public EasyExcelUtils(String fileName, Class<? extends EasyExcelTemplateExcelVo> excelClass) {
        this.fileName = fileName;
        this.excelClass = excelClass;
        this.dataList = null;
    }

    /** 构造: 表名, Class, 数据 */
    public EasyExcelUtils(String fileName, Class<? extends EasyExcelTemplateExcelVo> excelClass, List<? extends EasyExcelTemplateExcelVo> dataList) {
        this.fileName = fileName;
        this.excelClass = excelClass;
        this.dataList = dataList;
    }

    // ------------------------------ Static ------------------------------

    /**
     * 导入 Excel 文件, 返回 Excel 类, 失败时返回 null 并下载含报错信息的 Excel 文件
     *
     * @param file       文件
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param excelClass Excel 类, 必须 extends {@link EasyExcelTemplateExcelVo}
     */
    public static <T extends EasyExcelTemplateExcelVo> List<T> importAsExcel
    (MultipartFile file, HttpServletRequest request, HttpServletResponse response, Class<T> excelClass) {
        EasyExcelTemplateListener<T, EasyExcelTemplateEntity> listener = new EasyExcelTemplateListener<>(excelClass);
        if (Boolean.TRUE.equals(EasyExcelUtilsProtected.importExcel(file, request, response, excelClass, listener))) {
            return listener.getValidExcelList();
        }
        return null;
    }

    /**
     * 导入 Excel 文件, 返回 Entity 类, 失败时返回 null 并下载含报错信息的 Excel 文件
     *
     * @param file        文件
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     * @param excelClass  Excel 类, 必须 extends {@link EasyExcelTemplateExcelVo}
     * @param entityClass Excel 对应 Entity 类, 必须 extends {@link EasyExcelTemplateEntity}
     */
    public static <T extends EasyExcelTemplateExcelVo, U extends EasyExcelTemplateEntity> List<U> importAsEntity
    (MultipartFile file, HttpServletRequest request, HttpServletResponse response, Class<T> excelClass, Class<U> entityClass) {
        EasyExcelTemplateListener<T, U> listener = new EasyExcelTemplateListener<>(excelClass, entityClass);
        if (Boolean.TRUE.equals(EasyExcelUtilsProtected.importExcel(file, request, response, excelClass, listener))) {
            return listener.getValidEntityList();
        }
        return null;
    }

    /**
     * 导入 Excel 文件, 使用自定义的 ExcelListener 配置
     *
     * @param file       文件
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param excelClass Excel 类, 必须 extends {@link EasyExcelTemplateExcelVo}
     * @param listener   ExcelListener, 允许 extends {@link EasyExcelTemplateListener}
     */
    public static <T extends EasyExcelTemplateExcelVo, U extends EasyExcelTemplateListener<T, ? extends EasyExcelTemplateEntity>> void importExcel
    (MultipartFile file, HttpServletRequest request, HttpServletResponse response, Class<T> excelClass, U listener) {
        EasyExcelUtilsProtected.importExcel(file, request, response, excelClass, listener);
    }

    /**
     * 导出 Excel, 使用 Excel 类数据
     *
     * @param request       HttpServletRequest
     * @param response      HttpServletResponse
     * @param fileName      文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param excelClass    Excel 类, 必须 extends {@link EasyExcelTemplateExcelVo}
     * @param excelDataList 数据, 格式需与 excelClass 保持一致
     */
    public static <T extends EasyExcelTemplateExcelVo> void exportData(HttpServletRequest request, HttpServletResponse response,
                                                                       String fileName, Class<T> excelClass, List<T> excelDataList) {
        // 默认不包含报错信息
        Set<String> defaultExcludedCols = new HashSet<>();
        defaultExcludedCols.add(EasyExcelConstants.DEFAULT_ERROR_PARAM);

        EasyExcelUtilsProtected.exportExcel(request, response, fileName, null, excelClass, excelDataList,
                null, null, defaultExcludedCols, null, null, null, null);
    }

    /**
     * 导出 Excel, 使用 Entity 类数据
     *
     * @param request        HttpServletRequest
     * @param response       HttpServletResponse
     * @param fileName       文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param excelClass     Excel 类, 必须 extends {@link EasyExcelTemplateExcelVo}
     * @param entityClass    Excel 对应 Entity 类, 必须 extends {@link EasyExcelTemplateEntity}
     * @param entityDataList 数据, 格式需与 entityClass 保持一致
     */
    public static <T extends EasyExcelTemplateExcelVo, U extends EasyExcelTemplateEntity> void exportData
    (HttpServletRequest request, HttpServletResponse response, String fileName, Class<T> excelClass, Class<U> entityClass, List<U> entityDataList) {
        // 默认不包含报错信息
        Set<String> defaultExcludedCols = new HashSet<>();
        defaultExcludedCols.add(EasyExcelConstants.DEFAULT_ERROR_PARAM);

        // 将 Excel 数据转换为 Entity 数据
        List<T> excelDataList = new LinkedList<>();
        try {
            for (U currEntity : entityDataList) {
                T currExcel = excelClass.newInstance();
                BeanUtils.copyProperties(currEntity, currExcel);
                currExcel.setParamsAfterCopy(currEntity);
                excelDataList.add(currExcel);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("转换 Excel 失败, 请检查 " + excelClass.getName() + " 与 " + entityClass.getName(), e);
        }

        EasyExcelUtilsProtected.exportExcel(request, response, fileName, null, excelClass, excelDataList,
                null, null, defaultExcludedCols, null, null, null, null);
    }

    /**
     * 导出 Excel 模板: 数据为空
     *
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param fileName   文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param excelClass Excel 类
     */
    public static void exportTemplate(HttpServletRequest request, HttpServletResponse response, String fileName, Class<?> excelClass) {
        // 默认不包含报错信息
        Set<String> defaultExcludedCols = new HashSet<>();
        defaultExcludedCols.add(EasyExcelConstants.DEFAULT_ERROR_PARAM);

        EasyExcelUtilsProtected.exportExcel(request, response, fileName, null, excelClass, null,
                null, null, defaultExcludedCols, null, null, ExcelColWidthEnums.COL_WIDTH_HEAD, null);
    }

    /**
     * 导出 Excel 模板: 数据为空, 可添加说明
     *
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param fileName   文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param excelClass Excel 类
     * @param note       需要添加的填表说明, 位于列名之上, 可为空
     */
    public static void exportTemplate(HttpServletRequest request, HttpServletResponse response, String fileName, Class<?> excelClass, String note) {
        // 默认不包含报错信息
        Set<String> defaultExcludedCols = new HashSet<>();
        defaultExcludedCols.add(EasyExcelConstants.DEFAULT_ERROR_PARAM);

        EasyExcelUtilsProtected.exportExcel(request, response, fileName, null, excelClass, null,
                null, note, defaultExcludedCols, null, null, ExcelColWidthEnums.COL_WIDTH_HEAD, null);
    }

    // ------------------------------ Non-Static ------------------------------

    /** 将指定列加入屏蔽 */
    public void addExcludedCols(String columnName) {
        if (StringUtils.isNotBlank(columnName)) {
            if (this.excludedCols == null) {
                this.excludedCols = new HashSet<>();
            }
            this.excludedCols.add(columnName);
        }
    }

    /** 仅保留指定的列 */
    public void setIncludedCols(Class<?> targetClass, Set<String> includedCols) {
        if (includedCols == null || includedCols.isEmpty()) {
            return;
        }
        // 获取 Class 所有字段
        Set<String> excluded = new HashSet<>();
        Field[] fieldArray = targetClass.getDeclaredFields();
        for (Field field : fieldArray) {
            if (!includedCols.contains(field.getName())) {
                excluded.add(field.getName());
            }
        }
        this.excludedCols.addAll(excluded);
    }

    /** 导出 Excel: 使用当前 Excel 配置 */
    public void downloadCustomizedExcel(HttpServletRequest request, HttpServletResponse response) {
        EasyExcelUtilsProtected.exportExcel(request, response, fileName, sheetName, excelClass, dataList,
                title, note, excludedCols, headMap, dynamicDropDownMap, widthStrategy, useExcel07);
    }

    // ------------------------------ Private ------------------------------
}