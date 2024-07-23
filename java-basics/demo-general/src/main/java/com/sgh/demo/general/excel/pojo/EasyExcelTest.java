package com.sgh.demo.general.excel.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.sgh.demo.general.excel.easyexcel.EasyExcelClassTemplate;
import com.sgh.demo.general.excel.easyexcel.annotation.ExcelDropDown;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * EasyExcel 测试类
 *
 * @author Song gh
 * @version 2024/2/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("EasyExcel 测试类")
public class EasyExcelTest extends EasyExcelClassTemplate {

    @NotNull(message = "参数名称不可为空")
    @ExcelProperty("参数名称")
    private String name;

    @ExcelProperty("参数备注")
    private String comment = "";

    @NotNull(message = "类型不可为空")
    @ExcelDropDown(dynamicMenuName = "动态head")
    @ExcelProperty("类型")
    private String type;

    @ExcelDropDown({"是", "否"})
    @ExcelProperty("是否必填")
    private String required = "";

    @ExcelDropDown({"是", "否"})
    @ExcelProperty("是否主键")
    private String primaryKey = "";

    @ExcelDropDown(cascadeGroupName = "firstDropDown")
    @ExcelProperty("多级下拉框1")
    private String cascadeOne = "";

    @ExcelDropDown(cascadeGroupName = "firstDropDown")
    @ExcelProperty("多级下拉框2")
    private String cascadeTwo = "";

    @ExcelProperty("日期")
    @DateTimeFormat("yyyy-MM-dd")
    private Date date;

    @ExcelDropDown(cascadeGroupName = "firstDropDown")
    @ExcelProperty("多级下拉框3")
    private String cascadeThree = "";
}