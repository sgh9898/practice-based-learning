package com.demo.easyexcel.util.enums;

import lombok.Getter;

/**
 * Excel 列宽选取方式
 *
 * @author Song gh on 2022/10/17.
 */
@Getter
public enum EasyExcelColumnWidthEnums {

    /** 默认 */
    COLUMN_WIDTH_DEFAULT(0),
    /** 列宽以表头为准 */
    COLUMN_WIDTH_USE_HEAD(1),
    /** 列宽以内容为准 */
    COLUMN_WIDTH_USE_CONTENT(2);

    private int type;

    EasyExcelColumnWidthEnums() {
    }

    EasyExcelColumnWidthEnums(int type) {
        this.type = type;
    }
}
