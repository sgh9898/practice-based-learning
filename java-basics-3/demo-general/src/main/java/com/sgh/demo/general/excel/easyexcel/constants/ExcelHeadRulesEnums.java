package com.sgh.demo.general.excel.easyexcel.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * [枚举类] Excel 列名校验规则
 *
 * @author Song gh
 * @version 2025/1/7
 */
@Getter
@AllArgsConstructor
public enum ExcelHeadRulesEnums {

    /** [宽松] head 必须存在 @ExcelProperty 定义的有效字段, 允许存在未被定义的字段, 但会自动忽略 */
    CONTAINS,

    /** [严格] head 必须存在 @ExcelProperty 定义的有效字段, 且不存在未被定义的字段 */
    STRICTLY_CONTAINS
}
