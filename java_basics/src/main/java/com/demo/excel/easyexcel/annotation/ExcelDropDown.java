package com.demo.excel.easyexcel.annotation;

import com.demo.excel.easyexcel.pojo.EasyExcelExportDto;

import java.lang.annotation.*;

/**
 * Excel 下拉框注解
 *
 * @author Song gh
 * @version 2024/1/30
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelDropDown {

    /** 静态下拉框内容 */
    String[] value() default {};

    /** 列名, 用于配置动态下拉框, 需要与 {@link EasyExcelExportDto#dynamicDropDownMap} 的 key 保持一致 */
    String name() default "";
}

