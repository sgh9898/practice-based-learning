package com.demo.easyexcel.util.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.demo.easyexcel.util.EasyExcelUtils;
import com.demo.easyexcel.util.annotation.ExcelDropDown;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.Data;

import javax.persistence.Transient;
import javax.validation.constraints.*;
import java.util.Date;

/**
 * Excel 类, 用于 Excel[导入/导出], 需要 extends 之后使用
 * <br> 1. 配置中文列名: 使用 {@link ExcelProperty}, Date/String 类型数据可以使用 {@link DateTimeFormat} 指定格式
 * <br> 2. 配置下拉框: 1) 静态使用 {@link ExcelDropDown#value}
 * <br>              2) 动态使用 {@link ExcelDropDown#name} 并配置 {@link EasyExcelUtils#dynamicDropDownMap}
 * <br> 3. 自动校验: 1) 常用注解(注解中 message 作为未通过校验的返回信息): {@link NotNull}, {@link NotBlank}, {@link PositiveOrZero} 等
 * <br>             2) 完整注解: {@link javax.validation.constraints}
 *
 * @author Song gh on 2023/02/28.
 */
@Data
@HeadStyle(fillForegroundColor = 26)
@HeadFontStyle(fontHeightInPoints = 13)
@ContentStyle(wrapped = BooleanEnum.TRUE,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        horizontalAlignment = HorizontalAlignmentEnum.CENTER)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class EasyExcelTemplateExcelVo {

//    @NotBlank(message = "名称未填写")
//    // 多级列名会自动合并, 如 @ExcelProperty({"标题1", "标题2"}) 与 @ExcelProperty({"标题1", "标题3"}) 自动合并为"标题1"下"标题2""标题3"
//    @ExcelProperty("名称")
//    private String name;
//
//    @NotNull(message = "日期未填写")
//    @ExcelProperty("日期")
//    // 指定日期格式, 需要注意 Office 与 WPS 输入时日期格式默认为 "yyyy/MM/dd"
//    @DateTimeFormat("yyyy-MM-dd")
//    private Date dateTime;

    /** 报错返回字段, 用于展示 Excel 当前行未通过校验的原因 */
    @Transient
    @JsonIgnore
    @HeadStyle(fillForegroundColor = 47)
    @ExcelProperty("错误信息")
    public String defaultErrorMessage;

    /**
     * [使用默认配置时无须 override] 在默认的 BeanUtils.copyProperties 之后, 手动定义 Excel 中部分参数
     *
     * @param entity 需要转换的 Entity 类, 必须 extends {@link EasyExcelTemplateEntity}
     */
    public <T extends EasyExcelTemplateEntity> void setParamsAfterCopy(T entity) {
    }
}
