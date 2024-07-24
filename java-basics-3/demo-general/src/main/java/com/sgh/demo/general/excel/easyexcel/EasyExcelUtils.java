package com.sgh.demo.general.excel.easyexcel;


import com.alibaba.excel.ExcelWriter;
import com.sgh.demo.general.excel.easyexcel.constants.ExcelConstants;
import com.sgh.demo.general.excel.easyexcel.handler.ExcelColWidthStrategy;
import com.sgh.demo.general.excel.easyexcel.listener.ExcelListener;
import com.sgh.demo.general.excel.easyexcel.listener.ExcelNoModelListener;
import com.sgh.demo.general.excel.easyexcel.pojo.EasyExcelExportDto;
import com.sgh.demo.general.excel.easyexcel.pojo.EasyExcelNoModelExportDto;
import com.sgh.demo.general.excel.easyexcel.utils.BaseEasyExcelUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
 *     * 导出报错数据: {@link #exportErrorData}, 不再屏蔽默认的"报错信息"列
 *   3. 自定义导出:
 *     1) 配置参数(部分未列出): {@link EasyExcelExportDto}
 *     2) 导出:
 *       * 常规导出: {@link #exportExcel}, 默认的"报错信息"列会被屏蔽
 *       * 分页导出: {@link #writeSheet}--在新的一页写入数据, 数据写入全部完成后必须手动调用 {@link #closeExcel} 进行关闭
 *       * 导出报错数据: {@link #exportErrorExcel}, 不再屏蔽默认的"报错信息"列
 *
 * [不使用自定义的实体类 ExcelClass]
 *   1. 导入:
 *     * 导入数据(宽松的 head 校验): {@link #noModelImportExcel}
 *     * 导入数据(严格的 head 校验): {@link #noModelImportExcelStrictly}
 *   2. 导出:
 *     * 导出空白模板: {@link #noModelExportTemplate}
 *     * 导出数据: {@link #noModelExportData}
 *   3. 自定义导出:
 *     1) 配置常用参数(部分未列出): {@link EasyExcelNoModelExportDto}
 *     2) 导出:
 *       * 常规导出: {@link #noModelExportExcel}
 *       * 分页导出: {@link #noModelWriteSheet}--在新的一页写入数据, 数据写入全部完成后必须手动调用 {@link #closeExcel} 进行关闭 </pre>
 *
 * @author Song gh
 * @version 2024/1/30
 */
@Slf4j
@Getter
public class EasyExcelUtils {

// ------------------------------ 内部配置(不可修改) ------------------------------
    /** Excel 数据写入/编辑工具 */
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private ExcelWriter excelWriter;

    /** http servlet request */
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private HttpServletRequest request;

    /** http servlet response */
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private HttpServletResponse response;

    /** 表序号 */
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private Integer sheetIndex = 1;

// ------------------------------ 构造 ------------------------------

    /**
     * 常规构造(Static 方法不能满足需求时使用)
     *
     * @param request  http servlet request
     * @param response http servlet response
     * @param fileName 文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     */
    public EasyExcelUtils(HttpServletRequest request, HttpServletResponse response, @Nullable String fileName) {
        this.request = request;
        this.response = response;
        this.excelWriter = BaseEasyExcelUtils.createExcelWriter(request, response, fileName, false);
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
    public static <T> List<T> importExcel(MultipartFile file, HttpServletRequest request, HttpServletResponse response, Class<T> excelClass) {
        ExcelListener<T> excelListener = new ExcelListener<>(excelClass);
        // 导入并进行初步校验: 成功 --> 返回数据; 失败 --> 自动下载报错信息
        if (Boolean.TRUE.equals(BaseEasyExcelUtils.baseImportExcel(file, request, response, excelClass, excelListener, true, null))) {
            return excelListener.getValidList();
        }
        return new LinkedList<>();
    }

    /**
     * 导入 Excel 文件, 保存报错信息至 errorList
     * <pre>
     * 1. 从第一行有效的 head 开始读取, 自动跳过在此之前无效的行
     * 2. 导入完成后保存报错信息至 errorList </pre>
     *
     * @param file       文件
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param excelClass excel 实体类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param errorList  存储报错信息
     * @return 返回通过校验的数据, 未通过校验的数据存储于 errorList 中
     */
    public static <T> List<T> importExcelSaveError(@NonNull MultipartFile file, HttpServletRequest request, HttpServletResponse response,
                                                   Class<T> excelClass, @NonNull List<T> errorList) {
        ExcelListener<T> excelListener = new ExcelListener<>(excelClass);
        // 导入成功
        BaseEasyExcelUtils.baseImportExcel(file, request, response, excelClass, excelListener, false, errorList);
        return excelListener.getValidList();
    }

    /**
     * [不指定 ExcelClass] 导入 Excel 文件, 宽松的 head 校验(忽略多余的列名)
     * <pre>
     * 1. 从第一行有效的 head 开始读取, 自动跳过在此之前无效的行, 忽略未在 cnToEnHeadNameMap 中定义的 head
     * 2. 存在数据未通过校验时: 生成 Excel 并自动下载(仅包含未通过校验的数据) </pre>
     *
     * @param file              文件
     * @param cnToEnHeadNameMap 中英列名对照, Map(中文, 英文)
     * @return 全部通过校验时--返回数据; 存在报错数据时--返回空白List, 并以 Excel 形式下载报错数据
     */
    public static List<Map<String, Object>> noModelImportExcel(@NonNull MultipartFile file, @Nullable Map<String, String> cnToEnHeadNameMap) {
        ExcelNoModelListener excelNoModelListener = new ExcelNoModelListener(cnToEnHeadNameMap, ExcelConstants.HEAD_RULES_CONTAINS);
        // 导入成功
        if (Boolean.TRUE.equals(BaseEasyExcelUtils.noModelBaseImportExcel(file, excelNoModelListener, null))) {
            return excelNoModelListener.getValidList();
        }
        return new LinkedList<>();
    }

    /**
     * [不指定 ExcelClass] 导入 Excel 文件, 严格的 head 校验(不允许存在多余的列名)
     * <pre>
     * 1. 从第一行有效的 head 开始读取, 自动跳过在此之前无效的行, 忽略未在 cnToEnHeadNameMap 中定义的 head
     * 2. 存在数据未通过校验时: 生成 Excel 并自动下载(仅包含未通过校验的数据) </pre>
     *
     * @param file              文件
     * @param cnToEnHeadNameMap 中英列名对照, Map(中文, 英文)
     * @return 全部通过校验时--返回数据; 存在报错数据时--返回空白List, 并以 Excel 形式下载报错数据
     */
    public static List<Map<String, Object>> noModelImportExcelStrictly(@NonNull MultipartFile file, @Nullable Map<String, String> cnToEnHeadNameMap) {
        ExcelNoModelListener excelNoModelListener = new ExcelNoModelListener(cnToEnHeadNameMap, ExcelConstants.HEAD_RULES_STRICTLY_CONTAINS);
        // 导入成功
        if (Boolean.TRUE.equals(BaseEasyExcelUtils.noModelBaseImportExcel(file, excelNoModelListener, null))) {
            return excelNoModelListener.getValidList();
        }
        return new LinkedList<>();
    }

// ------------------------------ Static, 导出 ------------------------------

    /**
     * 导出 Excel, 使用自定义配置
     *
     * @param request       HttpServletRequest
     * @param response      HttpServletResponse
     * @param excelClass    Excel 类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param excelDataList 数据, 格式需与 excelClass 保持一致
     * @param exportDto     Excel 导出参数
     */
    public static <T> void exportExcel(HttpServletRequest request, HttpServletResponse response,
                                       Class<T> excelClass, @Nullable List<T> excelDataList, EasyExcelExportDto exportDto) {
        // 默认排除"错误信息"列
        exportDto.getExcludedCols().add(ExcelConstants.DEFAULT_ERROR_PARAM);

        BaseEasyExcelUtils.baseExportExcel(request, response, excelClass, exportDto, excelDataList);
    }

    /**
     * 导出 Excel 数据
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
        EasyExcelExportDto exportDto = new EasyExcelExportDto();
        exportDto.setFileName(fileName);
        exportDto.setSheetName(ExcelConstants.DEFAULT_SHEET_NAME);
        // 默认排除"错误信息"列
        exportDto.getExcludedCols().add(ExcelConstants.DEFAULT_ERROR_PARAM);

        BaseEasyExcelUtils.baseExportExcel(request, response, excelClass, exportDto, excelDataList);
    }

    /**
     * 导出含有报错的 Excel(不再屏蔽"错误信息"列)
     *
     * @param request       HttpServletRequest
     * @param response      HttpServletResponse
     * @param fileName      文件名, 有后缀时不做处理, 无后缀时自动补充"时间 + .xlsx"
     * @param excelClass    Excel 类, 推荐 extends {@link EasyExcelClassTemplate}
     * @param excelDataList 数据, 格式需与 excelClass 保持一致
     */
    public static <T> void exportErrorData(HttpServletRequest request, HttpServletResponse response,
                                           @Nullable String fileName, Class<T> excelClass, @NonNull List<T> excelDataList) {
        // 配置参数
        EasyExcelExportDto exportDto = new EasyExcelExportDto();
        exportDto.setFileName(fileName);
        exportDto.setSheetName(ExcelConstants.DEFAULT_SHEET_NAME);

        BaseEasyExcelUtils.baseExportExcel(request, response, excelClass, exportDto, excelDataList);
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
                                            Class<T> excelClass, @Nullable List<T> excelDataList, EasyExcelExportDto exportDto) {
        BaseEasyExcelUtils.baseExportExcel(request, response, excelClass, exportDto, excelDataList);
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
        EasyExcelExportDto exportDto = new EasyExcelExportDto();
        exportDto.setFileName(fileName);
        exportDto.setSheetName(ExcelConstants.DEFAULT_SHEET_NAME);
        exportDto.setNote(note);
        exportDto.setWidthStrategy(ExcelColWidthStrategy.COL_WIDTH_HEAD);
        // 默认排除"错误信息"列
        exportDto.getExcludedCols().add(ExcelConstants.DEFAULT_ERROR_PARAM);

        BaseEasyExcelUtils.baseExportExcel(request, response, excelClass, exportDto, null);
    }

    /**
     * [不指定 ExcelClass] 导出 Excel, 使用自定义配置
     *
     * @param request   http servlet request
     * @param response  http servlet response
     * @param exportDto Excel 导出参数
     */
    public static void noModelExportExcel(HttpServletRequest request, HttpServletResponse response, EasyExcelNoModelExportDto exportDto) {
        BaseEasyExcelUtils.noModelBaseExportExcel(request, response, exportDto);
    }

    /**
     * [不指定 ExcelClass] 导出 Excel 数据
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
        EasyExcelNoModelExportDto exportDto = new EasyExcelNoModelExportDto();
        exportDto.setFileName(fileName);
        exportDto.setEnSimpleHeadList(headList);
        if (enToCnHeadMap != null) {
            exportDto.setEnToCnHeadMap(enToCnHeadMap);
        }
        if (dataList != null) {
            exportDto.setDataList(dataList);
        }

        BaseEasyExcelUtils.noModelBaseExportExcel(request, response, exportDto);
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
        EasyExcelNoModelExportDto exportDto = new EasyExcelNoModelExportDto();
        exportDto.setFileName(fileName);
        exportDto.setEnSimpleHeadList(headList);
        exportDto.setNote(note);

        BaseEasyExcelUtils.noModelBaseExportExcel(request, response, exportDto);
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
    public void writeSheet(Class<?> excelClass, List<?> dataList, EasyExcelExportDto exportDto) {
        // 默认排除"错误信息"列
        exportDto.getExcludedCols().add(ExcelConstants.DEFAULT_ERROR_PARAM);
        BaseEasyExcelUtils.baseWriteSheet(excelWriter, excelClass, sheetIndex, exportDto, dataList);
        sheetIndex++;
    }

    /** [不指定 ExcelClass] 分页导出 Excel: 写入新的 sheet, 全部导出完成后必须手动调用 {@link #closeExcel} 关闭流 */
    public void noModelWriteSheet(EasyExcelNoModelExportDto exportDto) {
        BaseEasyExcelUtils.noModelBaseWriteSheet(excelWriter, sheetIndex, exportDto);
        sheetIndex++;
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
            throw new IllegalStateException("Excel 关闭输出流失败");
        }
    }
}