package com.collin.demo.common.excel.easyexcel.annotation;

import com.collin.demo.common.excel.easyexcel.pojo.EasyExcelExportDto;

import java.lang.annotation.*;

/**
 * Excel 下拉框注解
 *
 * @author Song gh
 * @version 2024/1/30
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelDropDown {

    /** 静态下拉框内容 */
    String[] value() default {};

    /**
     * 动态下拉框名称
     * <pre>
     * 1. 仅用于匹配动态下拉框, 不用于展示
     * 2. 需要与 {@link EasyExcelExportDto#getDynamicMenuMap() 动态下拉框} 的 key 保持一致 </pre>
     */
    String dynamicMenuName() default "";

    /**
     * 联动下拉框组名
     * <pre>
     * 1. 存在联动关系的下拉框属于同一组, 如: 省市区; 不同组之间必须使用不同的组名
     * 2. 联动层级顺序: 从父级到子级 = Excel 实体类从上到下 = 导出后 Excel 从左到右
     */
    String cascadeGroupName() default "";
}

