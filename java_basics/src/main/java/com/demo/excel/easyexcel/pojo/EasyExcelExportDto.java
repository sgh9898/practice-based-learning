package com.demo.excel.easyexcel.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.demo.excel.easyexcel.EasyExcelClassTemplate;
import com.demo.excel.easyexcel.annotation.ExcelDropDown;
import com.demo.excel.easyexcel.handler.ExcelColWidthStrategy;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    /** 动态下拉框, Map(列名, 选项); 其中列名必须使用 {@link ExcelDropDown#name} 在 ExcelClass 进行定义 */
    @NonNull
    private Map<String, String[]> dynamicDropDownMap = new HashMap<>();

    /** 需要替换的中文列名, Map(旧列名, 新列名); 其中旧列名必须使用 {@link ExcelProperty#value} 在 ExcelClass 进行定义 */
    @NonNull
    private Map<String, String> replaceHeadMap = new HashMap<>();

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

    /** 仅导出指定列(字段英文名) */
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
}
