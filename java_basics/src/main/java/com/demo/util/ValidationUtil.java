package com.demo.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 实体类字段校验工具
 *
 * @author Song gh on 2022/9/28.
 */
public class ValidationUtil {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** 校验实体类: 通过返回 null, 未通过返回报错 */
    public static String validate(Object entity) {
        Set<ConstraintViolation<Object>> violationSet = validator.validate(entity);
        for (ConstraintViolation<Object> violation : violationSet) {
            return violation.getMessage();
        }
        return null;
    }

    /**
     * 校验实体类单个字段: 通过返回 null, 未通过返回报错
     *
     * @param entity       实体类
     * @param propertyName 字段名
     */
    public static String validateProperty(Object entity, String propertyName) {
        Set<ConstraintViolation<Object>> violationSet = validator.validateProperty(entity, propertyName);
        for (ConstraintViolation<Object> violation : violationSet) {
            return violation.getMessage();
        }
        return null;
    }
}
