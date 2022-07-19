package com.demo.easyexcel.annotation;

import java.lang.annotation.*;

/**
 * Excel 下拉框注解, 用于标注字段
 *
 * @author Song gh on 2022/7/12.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelDropDown {

    /** 静态下拉框内容 */
    String[] options() default {};

    /** 名称, 用于配置动态下拉框 */
    String name() default "";
}

