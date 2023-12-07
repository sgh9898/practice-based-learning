package com.demo.excel.easyexcel;

/**
 * EasyExcel 相关常数
 *
 * @author Song gh on 2023/3/1.
 */
class ZippedEasyExcelConstants {

    /**
     * 默认 [报错信息] 字段名
     *
     * @see EasyExcelClassTemplate#defaultExcelErrorMessage
     */
    public static final String DEFAULT_ERROR_PARAM = "defaultExcelErrorMessage";

// ------------------------------ head 校验规则 ------------------------------

    /**
     * 当前行 head 中存在 @ExcelProperty 定义的有效字段, 且不存在未被定义的字段
     *
     * @see EasyExcelClassTemplate
     * @see com.alibaba.excel.annotation.ExcelProperty
     */
    public static final Integer HEAD_RULES_STRICTLY_CONTAINS = 0;

    /**
     * 当前行 head 中存在 @ExcelProperty 定义的有效字段, 自动忽略未被定义的字段
     *
     * @see EasyExcelClassTemplate
     * @see com.alibaba.excel.annotation.ExcelProperty
     */
    public static final Integer HEAD_RULES_CONTAINS = 1;
}
