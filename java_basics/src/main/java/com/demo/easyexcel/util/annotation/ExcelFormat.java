package com.demo.easyexcel.util.annotation;

import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;

import java.lang.annotation.*;

/**
 * Excel 格式注解
 *
 * @author Song gh on 2022/7/12.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@HeadStyle(fillForegroundColor = 26)
@HeadFontStyle(fontHeightInPoints = 13)
@ContentStyle(wrapped = BooleanEnum.TRUE,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        horizontalAlignment = HorizontalAlignmentEnum.CENTER)
public @interface ExcelFormat {
}

