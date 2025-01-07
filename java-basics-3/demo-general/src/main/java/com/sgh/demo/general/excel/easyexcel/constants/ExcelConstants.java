package com.sgh.demo.general.excel.easyexcel.constants;

import com.sgh.demo.general.excel.easyexcel.EasyExcelClassTemplate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * EasyExcel 相关常数
 *
 * @author Song gh
 * @version 2023/1/30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
}
