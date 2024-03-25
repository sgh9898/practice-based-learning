package com.collin.demo.excel.easyexcel.pojo;

import com.collin.demo.excel.easyexcel.handler.ExcelColWidthStrategy;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * [不指定 ExcelClass] EasyExcel 导出时的参数配置
 *
 * @author Song gh
 * @version 2024/1/30
 */
@Data
public class EasyExcelNoModelExportDto {

    /** 文件名, 无后缀时自动补充"时间 + .xlsx" */
    @Nullable
    private String fileName;

    /** 页名称 */
    @Nullable
    private String sheetName;

    /** 标题(位于最上方, 自定义说明与列名之上) */
    @Nullable
    private String title;

    /** 自定义说明(位于列名之上, 标题之下) */
    @Nullable
    private String note;

    /**
     * 单行英文列名
     * <br> 1. 需配合 {@link #enToCnHeadMap 列名对照} 转换为中文
     * <br> 2. 优先级低于 {@link #enHeadList 多行英文列名}
     */
    @NonNull
    private List<String> enSimpleHeadList = new LinkedList<>();

    /**
     * 多行英文列名, 相邻单元格的同名列名会自动合并
     * <pre>
     * 1. 需配合 {@link #enToCnHeadMap 列名对照} 转换为中文
     * 2. 不为空时会覆盖 {@link #enSimpleHeadList 单行英文列名}
     * 3. 示例: [["标题1", "标题2"], ["标题1", "标题3"]], 其中同名的"标题1"会自动合并, 下方为"标题2"和"标题3" </pre>
     */
    @NonNull
    private List<List<String>> enHeadList = new LinkedList<>();

    /** 列名英文转中文, 配合 {@link #enSimpleHeadList 单行英文列名} 或 {@link #enHeadList 多行英文列名} 生效 */
    @NonNull
    private Map<String, String> enToCnHeadMap = new HashMap<>();

    /** 下拉框, Map(英文列名, 选项) */
    @NonNull
    private Map<String, String[]> dropDownMap = new HashMap<>();

    /** 需要重点标注的列名(英文, 标为橘红色) */
    @NonNull
    private Set<String> importantHeadSet = new HashSet<>();

    /**
     * 数据
     * <br> 1. 每个 map 代表一行数据
     * <br> 2. key 需要在 {@link #enSimpleHeadList 单行英文列名} 或 {@link #enHeadList 多行英文列名} 之中
     */
    @NonNull
    private List<Map<String, Object>> dataList = new LinkedList<>();

    /** 列宽选取方式 */
    @Nullable
    private ExcelColWidthStrategy widthStrategy;

    /**
     * 是否使用 2007 版 Excel(仅在出现兼容问题时考虑使用, 默认使用 2003 版)
     * <pre>
     * 2007 版 excel 对应 ContentType: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
     * 2003 版 excel 对应 ContentType: application/vnd.ms-excel </pre>
     */
    @NonNull
    private Boolean useExcel07 = false;

    /**
     * 联动下拉框位置, 支持多组, Map(组名, 英文列名)
     * <pre>
     * 1. 存在联动关系的下拉框属于同一组, 如: 省市区
     * 2. 需要配合 {@link #cascadeMenuMap 联动下拉框选项} 使用 </pre>
     */
    @NonNull
    private Map<String, List<String>> cascadeColMap = new HashMap<>();

    /**
     * 联动下拉框选项, 支持多组, Map(组名, 选项)
     * <pre>
     * 1. 存在联动关系的下拉框属于同一组, 如: 省市区
     * 2. 需要配合 {@link #cascadeColMap 联动下拉框位置} 使用 </pre>
     */
    @NonNull
    private Map<String, List<ExcelCascadeOption>> cascadeMenuMap = new HashMap<>();
}
