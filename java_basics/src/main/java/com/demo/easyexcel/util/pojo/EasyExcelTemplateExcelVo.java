package com.demo.easyexcel.util.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Excel 类, 用于 Excel[导入/导出]
 * <br> 非必填项需设置默认值, 防止 null 报错
 *
 * @author Song gh on 2023/02/28.
 */
@Data
@HeadStyle(fillForegroundColor = 26)
@HeadFontStyle(fontHeightInPoints = 12)
@ContentStyle(wrapped = BooleanEnum.TRUE,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        horizontalAlignment = HorizontalAlignmentEnum.CENTER)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class EasyExcelTemplateExcelVo {

    /** 默认报错返回 */
    @HeadStyle(fillForegroundColor = 47)
    @ExcelProperty("错误信息")
    public String defaultErrorMessage;
}
