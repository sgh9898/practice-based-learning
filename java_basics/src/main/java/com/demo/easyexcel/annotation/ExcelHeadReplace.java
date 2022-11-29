package com.demo.easyexcel.annotation;

import java.lang.annotation.*;

/**
 * Excel 注解, 用于替换列名
 *
 * @author Song gh on 2022/10/11.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelHeadReplace {

    /** 名称, 作为匹配动态列名的标识, 必须在 @ExcelProperty 出现过 */
    String value();
}
