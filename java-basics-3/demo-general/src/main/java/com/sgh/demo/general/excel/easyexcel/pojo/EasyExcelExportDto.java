package com.sgh.demo.general.excel.easyexcel.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.sgh.demo.general.excel.easyexcel.EasyExcelClassTemplate;
import com.sgh.demo.general.excel.easyexcel.annotation.ExcelDropDown;
import com.sgh.demo.general.excel.easyexcel.handler.ExcelColWidthStrategy;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.*;

/**
 * EasyExcel 配合 ExcelClass 导出时的参数配置
 *
 * @author Song gh
 * @version 2024/1/30
 * @see EasyExcelClassTemplate 模板
 */
@Data
public class EasyExcelExportDto {

    /** 文件名, 无后缀时自动补充"时间 + .xlsx" */
    @Nullable
    private String fileName;

    /** 页名称 */
    @Nullable
    private String sheetName;

    /** 导出时排除的列名(ExcelClass 字段英文原名) */
    @NonNull
    private Set<String> excludedCols = new HashSet<>();

    /** 标题(位于最上方, 自定义说明与列名之上) */
    @Nullable
    private String title;

    /** 自定义说明(位于列名之上, 标题之下) */
    @Nullable
    private String note;

    /** 动态下拉框, Map(列名, 选项); 其中列名必须使用 {@link ExcelDropDown#dynamicMenuName} 在 ExcelClass 进行定义 */
    @NonNull
    private Map<String, String[]> dynamicMenuMap = new HashMap<>();

    /** 需要替换的中文列名, Map(旧列名, 新列名); 其中旧列名必须使用 {@link ExcelProperty#value} 在 ExcelClass 进行定义 */
    @NonNull
    private Map<String, String> replaceHeadMap = new HashMap<>();

    /** 列宽选取方式 */
    @Nullable
    private ExcelColWidthStrategy widthStrategy;

    /** 是否自动调整行高(需要手动指定时设为 false 即可) */
    @Nullable
    private Boolean autoRowHeight = true;

    /**
     * 是否使用 2007 版 Excel(仅在出现兼容问题时考虑使用, 默认使用 2003 版)
     * <pre>
     * 2007 版 excel 对应 ContentType: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
     * 2003 版 excel 对应 ContentType: application/vnd.ms-excel </pre>
     */
    @NonNull
    private Boolean useExcel07 = false;

    /**
     * 联动下拉框, 单组
     * <pre>
     * 1. 存在联动关系的下拉框属于同一组, 如: 省市区
     * 2. 组名需要使用 {@link ExcelDropDown#cascadeGroupName} 配置
     * 3. 存在多组联动下拉框时, 需要使用 {@link #cascadeMenuMap} 进行配置
     * 4. {@link #cascadeMenuMap} 会优先于本参数生效 </pre>
     */
    @NonNull
    private List<ExcelCascadeOption> cascadeMenu = new LinkedList<>();

    /**
     * 联动下拉框, 多组, Map(组名, 选项)
     * <pre>
     * 1. 存在联动关系的下拉框属于同一组, 如: 省市区
     * 2. 组名需要与 {@link ExcelDropDown#cascadeGroupName} 保持一致
     * 3. 会覆盖 {@link #cascadeMenu} 的效果 </pre>
     */
    @NonNull
    private Map<String, List<ExcelCascadeOption>> cascadeMenuMap = new HashMap<>();

    /**
     * 导出时仅保留指定列(ExcelClass 字段英文原名)
     *
     * @see #excludedCols
     */
    public void setIncludedCols(Class<?> targetClass, Set<String> includedCols) {
        // 获取 Class 所有字段
        Set<String> excluded = new HashSet<>();
        Field[] fieldArray = targetClass.getDeclaredFields();

        if (includedCols == null || includedCols.isEmpty()) {
            // 正选的列为空则不导出
            for (Field field : fieldArray) {
                excluded.add(field.getName());
            }
        } else {
            // 根据正选的列配置排除的列
            for (Field field : fieldArray) {
                if (!includedCols.contains(field.getName())) {
                    excluded.add(field.getName());
                }
            }
        }
        this.excludedCols.addAll(excluded);
    }
}
