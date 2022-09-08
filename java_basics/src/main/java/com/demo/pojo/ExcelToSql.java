package com.demo.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.BooleanEnum;
import com.demo.easyexcel.annotation.ExcelDropDown;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@ContentStyle(wrapped = BooleanEnum.TRUE)
public class ExcelToSql {

    @ExcelProperty(value = "参数名称")
    @DateTimeFormat("yyyy-MM-dd")
    private String name;

    @ExcelProperty(value = "参数备注")
    private String comment = "";

    @ExcelProperty(value = "类型")
    private String type;

    @ExcelDropDown(options = {"是", "否"})
    @ExcelProperty(value = "是否必填")
    private String required = "";

    @ExcelDropDown(name = "是否主键", options = {"是", "否"})
    @ExcelProperty(value = "是否主键")
    private String primaryKey = "";
}
