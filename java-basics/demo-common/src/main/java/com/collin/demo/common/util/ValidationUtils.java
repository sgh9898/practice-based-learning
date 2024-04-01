package com.collin.demo.common.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 实体类字段校验工具
 *
 * @author Song gh
 * @version 2022/9/28
 */
public class ValidationUtils {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 校验实体类字段
     *
     * @return 通过返回 null, 未通过返回报错, 多条报错只返回其中一条
     */
    public static String validate(Object entity) {
        Set<ConstraintViolation<Object>> violationSet = validator.validate(entity);
        if (violationSet != null && !violationSet.isEmpty()) {
            return violationSet.iterator().next().getMessage();
        }
        return null;
    }

    /**
     * 校验实体类单个字段: 通过返回 null, 未通过返回报错
     *
     * @param entity       实体类
     * @param propertyName 字段名
     * @return 通过返回 null, 未通过返回报错, 多条报错只返回其中一条
     */
    public static String validateProperty(Object entity, String propertyName) {
        Set<ConstraintViolation<Object>> violationSet = validator.validateProperty(entity, propertyName);
        if (violationSet != null && !violationSet.isEmpty()) {
            return violationSet.iterator().next().getMessage();
        }
        return null;
    }

    private ValidationUtils() {
    }
}
