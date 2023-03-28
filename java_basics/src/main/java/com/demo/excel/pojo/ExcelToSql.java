package com.demo.excel.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.BooleanEnum;
import com.demo.excel.easyexcel.ExcelDropDown;
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

    @ExcelProperty("参数名称")
    private String name;

    @ExcelProperty("参数备注")
    private String comment = "";

    @ExcelProperty("类型")
    private String type;

    @ExcelDropDown({"是", "否"})
    @ExcelProperty("是否必填")
    private String required = "";

    @ExcelDropDown({"是", "否"})
    @ExcelProperty("是否主键")
    private String primaryKey = "";
}
