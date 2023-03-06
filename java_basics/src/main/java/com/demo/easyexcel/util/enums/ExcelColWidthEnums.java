package com.demo.easyexcel.util.enums;

import lombok.Getter;

/**
 * Excel 列宽选取方式
 *
 * @author Song gh on 2022/10/17.
 */
@Getter
public enum ExcelColWidthEnums {

    /** 默认 */
    COL_WIDTH_DEFAULT(0),
    /** 列宽以 head 为准 */
    COL_WIDTH_HEAD(1),
    /** 列宽以内容为准 */
    COL_WIDTH_CONTENT(2);

    private int type;

    ExcelColWidthEnums() {
    }

    ExcelColWidthEnums(int type) {
        this.type = type;
    }
}
