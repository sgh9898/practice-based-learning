package com.collin.demo.common.excel.easyexcel.handler;

import lombok.Getter;

/**
 * Excel 列宽选取方式
 *
 * @author Song gh
 * @version 2024/1/30
 */
@Getter
public enum ExcelColWidthStrategy {

    /** 默认: 以本列最长的数据为准 */
    COL_WIDTH_DEFAULT,

    /** 列宽以 head 为准 */
    COL_WIDTH_HEAD,

    /** 列宽以内容为准 */
    COL_WIDTH_CONTENT;

    ExcelColWidthStrategy() {
    }
}
