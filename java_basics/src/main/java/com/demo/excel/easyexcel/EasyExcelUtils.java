package com.demo.excel.easyexcel;


import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * EasyExcel 工具类
 * <br> 0. 前置需求:
 * <br>     1) 指定 ExcelClass 时: extends {@link ExcelClassTemplate}(内有详细配置说明), 或将类注解与 defaultExcelErrorMessage 字段直接配置于实体类中
 * <br>     2) 不指定 ExcelClass 时不需要前置操作
 * <br> 1. 导入:
 * <br>     1) 返回 List(ExcelClass): {@link #importData}
 * <br> 2. 导出, Static:
 * <br>     1) 导出 Excel 空白模板: {@link #exportTemplate}
 * <br>     2) 导出 Excel 数据: {@link #exportData}
 * <br>     3) [不指定 ExcelClass] 导出空白模板: {@link #noModelExportTemplate}
 * <br>     3) [不指定 ExcelClass] 导出数据: {@link #noModelExportData}
 * <br> 3. 导出, Non-Static, 使用自定义配置:
 * <br>     1) 自定义配置: 使用 setter 进行配置; 通用配置始终生效, ExcelClass/NoModel 系列参数仅在对应方法中生效
 * <br>     2) ExcelClass 系列:
 * <br>         a. 导出 Excel(使用通用配置与 ExcelClass 相关配置): {@link #exportExcelCustomized}
 * <br>         b. 写入单张 Sheet: {@link #writeSheet}, 完成全部写入后必须手动调用 {@link #closeExcel} 关闭流
 * <br>     3) NoModel 系列(不指定 ExcelClass):
 * <br>         a. 导出 Excel(使用通用配置与 NoModel 相关配置): {@link #noModelExportExcelCustomized}
 * <br>         b. 写入单张 Sheet: {@link #noModelWriteSheet}, 完成全部写入后必须手动调用 {@link #closeExcel} 关闭流
 *
 * @author Song gh on 2023/3/1.
 */
@Slf4j
@Setter
public class EasyExcelUtils {

// ------------------------------ 内部配置 ------------------------------
    /** Excel 数据导出主体 */
    @Setter(AccessLevel.PRIVATE)
    private ExcelWriter excelWriter;

    /** http servlet request */
    @Setter(AccessLevel.PRIVATE)
    private HttpServletRequest request;

    /** http servlet response */
    @Setter(AccessLevel.PRIVATE)
    private HttpServletResponse response;

    /** 文件名 */
    @Setter(AccessLevel.PRIVATE)
    private String fileName;

    /** 表序号 */
    @Setter(AccessLevel.PRIVATE)
    private Integer sheetIndex = 0;

// ------------------------------ 通用配置 ------------------------------
    /** 表名 */
    private String sheetName;
    /** 标题(自定义说明行之上, 列名之上) */
    private String title;
    /** 自定义说明行(列名之上) */
    private String note;
    /** 列宽选取方式 */
    private ProtectedEnumsColWidth widthStrategy;
    /** 启用 07 版 Excel: 默认 false, 出现兼容问题时可尝试 true */
    private Boolean useExcel07;

// ------------------------------ ExcelClass 相关配置 ------------------------------
    /** Excel 实体类名 */
    private Class<?> excelClass;
    /** 导出数据 */
    private List<?> dataList = new LinkedList<>();
    /** 动态列名, 格式: Map(原名, 新名), 原名必须使用 {@link ExcelProperty#value} 配置在 ExcelClass 中 */
    private Map<String, String> dynamicHeadMap;
    /** 动态下拉框, 格式: Map(名称, 选项), 名称必须使用 {@link ExcelDropDown#name} 配置在 ExcelClass 中) */
    private Map<String, String[]> dynamicDropDownMap = new HashMap<>();
    /** 导出时排除的列名(ExcelClass 字段英文原名) */
    private Set<String> excludedCols = new HashSet<>();

// ------------------------------ NoModel 相关配置(不指定 ExcelClass) ------------------------------
    /**
     * [不指定 ExcelClass] 中文列名
     * <br> 1. 单行列名: List(string)
     * <br> 2. 多行复合列名(相同标题自动合并): List(List(string)); 如 List(List(第一行, 第二行1), List(第一行, 第二行2)), 其中"第一行"会自动合并
     */
    private List<?> noModelHeadList = new LinkedList<>();
    /**
     * [不指定 ExcelClass] 中英列名对照, Map(中文, 英文)
     * <br> 中文列名, 需要出现在 {@link #noModelHeadList} 中
     * <br> 英文字段, 需要出现在 {@link #noModelDataList} 中
     */
    private Map<String, String> noModelCnToEnHeadNameMap = new HashMap<>();
    /** [不指定 ExcelClass] 导出数据 */
    private List<Map<String, Object>> noModelDataList = new LinkedList<>();
    /** [不指定 ExcelClass] 下拉框, 格式: Map(列名, 选项), 列名需要出现在 {@link #noModelHeadList} 中) */
    private Map<String, String[]> noModelDropDownMap = new HashMap<>();

// ------------------------------ 构造 ------------------------------

    /**
     * 常规构造(Static 方法不能满足需求时使用)
     *
     * @param request    http servlet request
     * @param response   http servlet response
     * @param fileName   文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param excelClass Excel 实体类: extends {@link ExcelClassTemplate}, 或将类注解与 defaultExcelErrorMessage 字段直接配置于实体类中
     */
    public EasyExcelUtils(HttpServletRequest request, HttpServletResponse response, String fileName, Class<?> excelClass) {
        this.request = request;
        this.response = response;
        this.fileName = fileName;
        this.excelClass = excelClass;
        this.excelWriter = ProtectedEasyExcelUtils.createExcelWriter(request, response, fileName, false);
    }

    /**
     * 不指定 ExcelClass 构造(Static 方法不能满足需求时使用)
     *
     * @param request  http servlet request
     * @param response http servlet response
     * @param fileName 文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     */
    public EasyExcelUtils(HttpServletRequest request, HttpServletResponse response, String fileName) {
        this.request = request;
        this.response = response;
        this.fileName = fileName;
        this.excelWriter = ProtectedEasyExcelUtils.createExcelWriter(request, response, fileName, false);
    }

// ------------------------------ Static, 导入 ------------------------------

    /**
     * 导入 Excel 文件
     * <br> 1. 自动跳过无效的 head
     * <br> 2. 失败时返回 null 并下载含报错信息的 Excel 文件
     *
     * @param file       文件
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param excelClass Excel 类, 推荐 extends {@link ExcelClassTemplate}
     * @return List(ExcelClass) = 成功, false = 文件为空, null = 失败且下载含报错信息的 Excel 文件
     */
    public static <T> List<T> importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response, Class<T> excelClass) {
        Listener<T> listener = new Listener<>(excelClass);
        if (Boolean.TRUE.equals(ProtectedEasyExcelUtils.baseImportExcel(file, request, response, excelClass, listener))) {
            return listener.getValidList();
        }
        return null;
    }

    /**
     * [不指定 ExcelClass] 导入 Excel 文件
     * <br> 1. 自动跳过无效的 head
     * <br> 2. 失败时返回 null 并下载含报错信息的 Excel 文件
     *
     * @param file              文件
     * @param request           HttpServletRequest
     * @param response          HttpServletResponse
     * @param cnToEnHeadNameMap [允许空/null] 中英列名对照, Map(中文, 英文)
     * @return List(Map) = 成功, false = 文件为空, null = 失败且下载含报错信息的 Excel 文件
     */
    public static Object noModelImportExcel(MultipartFile file, HttpServletRequest request, HttpServletResponse response, Map<String, String> cnToEnHeadNameMap) {
        ListenerNoModel listenerNoModel = new ListenerNoModel(cnToEnHeadNameMap);
        if (Boolean.TRUE.equals(ProtectedEasyExcelUtils.noModelBaseImportExcel(file, request, response, listenerNoModel))) {
            return listenerNoModel.getValidList();
        }
        return null;
    }

// ------------------------------ Static, 导出 ------------------------------

    /**
     * 导出 Excel
     *
     * @param request       HttpServletRequest
     * @param response      HttpServletResponse
     * @param fileName      文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param excelClass    Excel 类, 推荐 extends {@link ExcelClassTemplate}
     * @param excelDataList [允许空/null] 数据, 格式需与 excelClass 保持一致
     */
    public static <T> void exportData(HttpServletRequest request, HttpServletResponse response, String fileName, Class<T> excelClass, List<T> excelDataList) {
        // 默认排除"错误信息"列
        Set<String> defaultExcludedCols = new HashSet<>();
        defaultExcludedCols.add(ProtectedConstants.DEFAULT_ERROR_PARAM);
        // 导出
        ProtectedEasyExcelUtils.baseExportExcel(request, response, excelClass, fileName, "Sheet1", excelDataList,
                null, null, defaultExcludedCols, null, null, null, null);
    }

    /**
     * 导出 Excel 模板: 数据为空, 可添加说明
     *
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param fileName   文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param excelClass Excel 类, 推荐 extends {@link ExcelClassTemplate}
     * @param note       [允许空/null] 需要添加的填表说明, 位于列名之上
     */
    public static void exportTemplate(HttpServletRequest request, HttpServletResponse response, String fileName, Class<?> excelClass, String note) {
        // 默认排除"错误信息"列
        Set<String> defaultExcludedCols = new HashSet<>();
        defaultExcludedCols.add(ProtectedConstants.DEFAULT_ERROR_PARAM);
        // 导出
        ProtectedEasyExcelUtils.baseExportExcel(request, response, excelClass, fileName, "Sheet1", null, null, note,
                defaultExcludedCols, null, null, ProtectedEnumsColWidth.COL_WIDTH_HEAD, null);
    }

    /**
     * [不指定 ExcelClass] 导出 Excel
     *
     * @param request        http servlet request
     * @param response       http servlet response
     * @param fileName       文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param headList       中文列名, 单行列名: List(string); 多行复合列名(相同标题自动合并): List(List(string));
     *                       例: List(List(第一行, 第二行1),List(第一行, 第二行2)),其中"第一行"会自动合并
     * @param noModelHeadMap 中英列名对照, Map(中文, 英文)
     * @param dataList       [允许空/null] 表内数据
     */
    public static void noModelExportData(HttpServletRequest request, HttpServletResponse response, String fileName, List<?> headList, Map<String, String> noModelHeadMap, List<Map<String, Object>> dataList) {
        // 整理列名与数据格式
        List<List<String>> newHeadList = new LinkedList<>();
        List<List<Object>> newDataList = new LinkedList<>();
        if (setHeadAndDataNoModel(headList, dataList, noModelHeadMap, newHeadList, newDataList)) {
            ProtectedEasyExcelUtils.noModelBaseExportExcel(request, response, fileName, "Sheet1", newHeadList, newDataList,
                    null, null, null, null, null);
        }
    }

    /**
     * [不指定 ExcelClass] 导出 Excel 模板: 数据为空, 可添加说明
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param fileName 文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param headList 单行列名 List(string); 多行复合列名 List(List(string) 第一行, List 第二行...)
     * @param note     [允许空/null] 需要添加的填表说明, 位于列名之上
     */
    public static void noModelExportTemplate(HttpServletRequest request, HttpServletResponse response, String fileName, List<?> headList, String note) {
        // 整理列名与数据格式
        List<List<String>> newHeadList = new LinkedList<>();
        if (setHeadAndDataNoModel(headList, null, null, newHeadList, null)) {
            ProtectedEasyExcelUtils.noModelBaseExportExcel(request, response, fileName, "Sheet1", newHeadList, null,
                    null, note, null, null, null);
        }
    }
// ============================== Static, 导出 End ==============================

// ------------------------------ Non-static ------------------------------

    /** 导出 Excel: (NoModel 系列参数不生效) */
    public void exportExcelCustomized() {
        writeSheet();
        closeExcel();
    }

    /** 导出 Excel: 写入单张 sheet(NoModel 系列参数不生效), 全部导出完成后必须手动调用 {@link #closeExcel} 关闭流 */
    public void writeSheet() {
        if (excelClass == null) {
            log.info("未指定 ExcelClass");
            throw new RuntimeException("未指定 ExcelClass");
        }
        // 默认排除"错误信息"列
        excludedCols.add(ProtectedConstants.DEFAULT_ERROR_PARAM);
        ProtectedEasyExcelUtils.baseWriteSheet(excelWriter, excelClass, sheetIndex, sheetName, dataList, excludedCols, title, note, dynamicDropDownMap, dynamicHeadMap, widthStrategy);
        sheetIndex++;
    }

    /** 导出 Excel: (NoModel 系列参数不生效) */
    public void noModelExportExcelCustomized() {
        noModelWriteSheet();
        closeExcel();
    }

    /** [不指定 ExcelClass] 导出 Excel: 写入单张 sheet(使用通用及 NoModel 系列参数) */
    public void noModelWriteSheet() {
        // 整理列名与数据格式
        List<List<String>> newHeadList = new LinkedList<>();
        List<List<Object>> newDataList = new LinkedList<>();
        if (setHeadAndDataNoModel(noModelHeadList, noModelDataList, noModelCnToEnHeadNameMap, newHeadList, newDataList)) {
            // 配置下拉框
            Map<Integer, String[]> indexedDropDownMap = new HashMap<>();
            int index = 1;
            for (List<String> head : newHeadList) {
                if (noModelDropDownMap.containsKey(head.get(head.size() - 1))) {
                    indexedDropDownMap.put(index, noModelDropDownMap.get(head.get(head.size() - 1)));
                }
                index++;
            }
            ProtectedEasyExcelUtils.noModelBaseWriteSheet(excelWriter, sheetIndex, sheetName, newHeadList, newDataList,
                    title, note, indexedDropDownMap, widthStrategy);
        }
        sheetIndex++;
    }

    /** 配置 Excel 兼容格式, 并重新生成 Excel Writer */
    public void setUseExcel07(Boolean useExcel07) {
        this.useExcel07 = useExcel07;
        this.excelWriter = ProtectedEasyExcelUtils.createExcelWriter(request, response, fileName, useExcel07);
    }

    /** 将指定列加入屏蔽 */
    public void addExcludedCols(String columnName) {
        if (StringUtils.isNotBlank(columnName)) {
            if (this.excludedCols == null) {
                this.excludedCols = new HashSet<>();
            }
            this.excludedCols.add(columnName);
        }
    }

    /** 设定屏蔽列(通过保留的列) */
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

    /** 关闭所有的流 */
    public void closeExcel() {
        if (excelWriter != null) {
            excelWriter.finish();
        }
        try {
            if (response.getOutputStream() != null) {
                response.getOutputStream().close();
            }
        } catch (IOException e) {
            log.error("Excel 关闭输出流失败", e);
            throw new RuntimeException("Excel 关闭输出流失败");
        }
    }

// ------------------------------ Private ------------------------------

    /**
     * 整理列名与数据格式(不指定 ExcelClass 时)
     *
     * @param srcHeadList  单行列名 List(string); 多行复合列名 List(List(string))
     * @param srcDataList  [允许空/null] 表内数据
     * @param destHeadList 整理后的列名
     * @param destDataList 整理后的数据
     */
    @SuppressWarnings("unchecked")
    private static boolean setHeadAndDataNoModel(List<?> srcHeadList, List<Map<String, Object>> srcDataList, Map<String, String> noModelHeadMap,
                                                 List<List<String>> destHeadList, List<List<Object>> destDataList) {
        // 不允许列名为空
        if (srcHeadList == null || srcHeadList.isEmpty()) {
            log.info("Excel 列名为空时无法导出");
            return false;
        }

        // 整理列名
        List<String> orderedChineseHead = new LinkedList<>();
        if (srcHeadList.get(0) instanceof String) {
            // 列名顺序
            orderedChineseHead = (List<String>) srcHeadList;
            // 单行列名
            for (String currHead : orderedChineseHead) {
                List<String> innerHeadList = new LinkedList<>();
                innerHeadList.add(currHead);
                destHeadList.add(innerHeadList);
            }
        } else if (srcHeadList.get(0) instanceof List) {
            // 多行列名
            destHeadList.addAll((List<List<String>>) srcHeadList);
            // 列名顺序
            for (List<String> currHead : destHeadList) {
                orderedChineseHead.add(currHead.get(currHead.size() - 1));
            }
        }

        // 根据列名顺序整理数据
        if (srcDataList != null && !srcDataList.isEmpty()) {
            for (Map<String, Object> currDataMap : srcDataList) {
                List<Object> innerDataList = new LinkedList<>();
                for (String chineseHeadName : orderedChineseHead) {
                    innerDataList.add(currDataMap.get(noModelHeadMap.get(chineseHeadName)));
                }
                destDataList.add(innerDataList);
            }
        }
        return true;
    }
}