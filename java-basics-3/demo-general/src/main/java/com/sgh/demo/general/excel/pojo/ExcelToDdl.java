package com.sgh.demo.general.excel.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.sgh.demo.general.excel.easyexcel.EasyExcelClassTemplate;
import com.sgh.demo.general.excel.easyexcel.annotation.ExcelDropDown;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Excel 转 sql 建表语句
 *
 * @author Song gh
 * @version 2024/2/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Excel 转 sql 建表语句")
public class ExcelToDdl extends EasyExcelClassTemplate {

    @ExcelProperty("参数名称")
    private String name;

    @ExcelProperty("参数备注")
    private String comment = "";

    @NotNull(message = "类型不可为空")
    @ExcelProperty("类型")
    private String type;

    @ExcelDropDown({"是", "否"})
    @ExcelProperty("是否必填")
    private String required = "";

    @ExcelDropDown({"是", "否"})
    @ExcelProperty("是否主键")
    private String primaryKey = "";
}