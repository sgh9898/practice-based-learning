package com.demo.easyexcel.util.annotation;

import java.lang.annotation.*;

/**
 * Excel 下拉框注解
 *
 * @author Song gh on 2022/7/12.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelDropDown {

    /** 静态下拉框内容 */
    String[] value() default {};

    /** 名称, 用于配置动态下拉框 */
    String name() default "";
}

