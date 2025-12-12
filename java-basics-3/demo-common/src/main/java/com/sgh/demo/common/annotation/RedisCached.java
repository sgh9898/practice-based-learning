package com.sgh.demo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [注解] Redis 缓存
 * <pre>
 *     1. 仅用于标注方法使用了 redis 缓存, 便于代码维护, 无任何实际功能
 * </pre>
 *
 * @author Song gh
 * @since 2025/8/5
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface RedisCached {

    /** 备注 */
    String value() default "";
}