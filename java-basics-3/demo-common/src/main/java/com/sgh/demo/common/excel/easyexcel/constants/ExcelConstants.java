package com.sgh.demo.common.excel.easyexcel.constants;

import com.sgh.demo.common.excel.easyexcel.EasyExcelClassTemplate;

/**
 * EasyExcel 相关常数
 *
 * @author Song gh
 * @version 2023/1/30
 */
public class ExcelConstants {

    /**
     * [默认字段] 报错信息
     *
     * @see EasyExcelClassTemplate#defaultExcelErrorMessage
     */
    public static final String DEFAULT_ERROR_PARAM = "defaultExcelErrorMessage";

    /** [默认] 页名 */
    public static final String DEFAULT_SHEET_NAME = "sheet1";

    /** [默认] 联动下拉框页名 */
    public static final String DEFAULT_CASCADE_SHEET_NAME = "cascade_data";

    /**
     * [校验规则/严格] 当前行 head 中存在 @ExcelProperty 定义的有效字段, 且不存在未被定义的字段
     *
     * @see EasyExcelClassTemplate
     * @see com.alibaba.excel.annotation.ExcelProperty
     */
    public static final Integer HEAD_RULES_STRICTLY_CONTAINS = 0;

    /**
     * [校验规则/宽松] 当前行 head 中存在 @ExcelProperty 定义的有效字段, 自动忽略未被定义的字段
     *
     * @see EasyExcelClassTemplate
     * @see com.alibaba.excel.annotation.ExcelProperty
     */
    public static final Integer HEAD_RULES_CONTAINS = 1;

    private ExcelConstants() {
    }
}
