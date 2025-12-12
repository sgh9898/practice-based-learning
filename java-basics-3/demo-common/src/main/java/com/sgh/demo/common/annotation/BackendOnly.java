package com.sgh.demo.common.annotation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * [注解] 仅后端使用
 * <pre>
 *     1. 被标注的字段不参与 Json 转换
 *     2. 被标注的字段不展示在接口文档中
 * </pre>
 *
 * @author Song gh
 * @since 2025/8/5
 */
@JsonIgnore
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BackendOnly {

    /** 被标注的字段不展示在接口文档中 */
    @AliasFor(annotation = ApiModelProperty.class, attribute = "hidden")
    boolean hidden() default true;
}