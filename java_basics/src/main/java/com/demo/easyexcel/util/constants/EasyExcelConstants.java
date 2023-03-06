package com.demo.easyexcel.util.constants;

import com.demo.easyexcel.util.pojo.EasyExcelTemplateExcelVo;

/**
 * EasyExcel 相关常数
 *
 * @author Song gh on 2023/3/1.
 */
public class EasyExcelConstants {

    /**
     * 默认 [报错信息] 字段名
     *
     * @see EasyExcelTemplateExcelVo#defaultErrorMessage
     */
    public static final String DEFAULT_ERROR_PARAM = "defaultErrorMessage";

    // ------------------------------ head 校验规则 ------------------------------
    /**
     * 当前行 head 中存在 @ExcelProperty 定义的有效字段
     *
     * @see EasyExcelTemplateExcelVo
     * @see com.alibaba.excel.annotation.ExcelProperty
     */
    public static final Integer HEAD_RULES_CONTAINS = 0;

    /**
     * 当前行 head 中存在 @ExcelProperty 定义的有效字段, 且不存在未被定义的字段
     *
     * @see EasyExcelTemplateExcelVo
     * @see com.alibaba.excel.annotation.ExcelProperty
     */
    public static final Integer HEAD_RULES_STRICTLY_CONTAINS = 1;
}
