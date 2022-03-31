package com.demo.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * Sql 建表格式 (from Excel)
 * <br> 非必填项添加默认值, 防止出现 null
 *
 * @author Song gh on 2022/3/25.
 */
@Data
@HeadFontStyle(fontHeightInPoints = 12)
@HeadRowHeight(30)
@ColumnWidth(12)
public class ExcelToSql {

    @ExcelProperty(value = "参数名称")
    private String name;

    @ExcelProperty(value = "参数备注")
    private String comment = "";

    @ExcelProperty(value = "类型")
    private String type;

    @ExcelProperty(value = "是否必填(填'是/否')")
    private String required = "";

    @ExcelProperty(value = "是否主键(填'是/否')")
    private String primaryKey = "";
}
