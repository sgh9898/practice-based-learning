package com.sgh.demo.general.excel.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sgh.demo.general.excel.easyexcel.annotation.ExcelDropDown;
import com.sgh.demo.general.excel.easyexcel.pojo.EasyExcelExportDto;
import lombok.Data;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * Excel 模板类: extends 本类直接使用; 或将类注解与 defaultExcelErrorMessage 字段添加到 ExcelClass 中
 * <pre>
 * 1. 配置中文列名:
 *   1) 使用 {@link ExcelProperty}, Date/String 类型数据可以使用 {@link DateTimeFormat} 指定格式
 *   2) 未配置 {@link ExcelProperty} 的列会自动忽略, 可用于控制导入/导出列
 * 2. 配置下拉框:
 *   1) 静态下拉框注解: {@link ExcelDropDown#value}
 *   2) 动态下拉框注解: {@link ExcelDropDown#dynamicMenuName}, 需要配置 {@link EasyExcelExportDto#getDynamicMenuMap()}
 * 3. 自动校验:
 *   1) 常用注解(注解中 message 作为未通过校验的返回信息): {@link NotNull}, {@link NotBlank}, {@link PositiveOrZero} 等
 *   2) 校验类注解目录: {@link javax.validation.constraints} </pre>
 *
 * @author Song gh
 * @version 2024/1/30
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
// ------------------------------ 格式 ------------------------------
@HeadStyle(fillForegroundColor = 26)
@HeadFontStyle(fontHeightInPoints = 13)
@ContentStyle(wrapped = BooleanEnum.TRUE,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        horizontalAlignment = HorizontalAlignmentEnum.CENTER)
@ContentFontStyle(fontHeightInPoints = 12)
// ============================== 格式 End ==============================
public abstract class EasyExcelClassTemplate {

//    示例:
//    @ColumnWidth(50)                         // 手动指定列宽
//    @NotBlank(message = "名称未填写")          // 初步校验, 未通过时报错信息会出现在报错返回的 Excel 中
//    @ExcelProperty("名称")                    // 多级列名会自动合并, 如 @ExcelProperty({"标题1", "标题2"}) 与 @ExcelProperty({"标题1", "标题3"}) 自动合并为"标题1"下"标题2""标题3"
//    @ExcelDropDown({"下拉选项1", "下拉选项2"})  // 下拉框
//    private String name;
//
//    @NotNull(message = "日期未填写")
//    @ExcelProperty("日期")
//    @DateTimeFormat("yyyy-MM-dd") // 指定日期格式, 需要注意 Office 与 WPS 输入时日期格式默认为 "yyyy/MM/dd"
//    private Date dateTime;

    /** 报错返回字段, 用于展示 Excel 当前行未通过校验的原因 */
    @Transient
    @JsonIgnore
    @HeadStyle(fillForegroundColor = 29)
    @HeadFontStyle(color = 1)
    @ExcelProperty("错误信息")
    public String defaultExcelErrorMessage;
}
