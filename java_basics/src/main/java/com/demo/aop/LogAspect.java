package com.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * 使用切面对已有日志功能进行扩充
 * <br> 切面类需要 {@link Aspect}, {@link Component} 注解
 *
 * @author Song gh on 2022/3/31.
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /** 切入点 */
//    // 当前范围: controller 目录下所有后缀为 Controller 文件中所有 public 方法
//    @Pointcut("execution(public * com.demo.controller.*Controller.*(..))")
    // 当前范围: GeneralController 文件中所有以 aop 开头的方法
    @Pointcut("execution(public * com.demo.aop.AopController.aop*(..))")
    public void controllerPoint() {
    }

    /** 被监控的方法调用前 */
    @Before("controllerPoint()")
    public void beforeCut(JoinPoint joinPoint) {
        log.info("[切面功能] [前置] 切入点: {}, 方法所属 Class: {}, 方法名: {}",
                joinPoint.toString(),
                joinPoint.getSignature().getDeclaringType(),
                joinPoint.getSignature().getName());
    }

    /** 被监控的方法调用后 */
    @After("controllerPoint()")
    public void afterCut(JoinPoint joinPoint) {
        log.info("[切面功能] [后置] 切入点: {}, 方法所属 Class: {}, 方法名: {}",
                joinPoint.toString(),
                joinPoint.getSignature().getDeclaringType(),
                joinPoint.getSignature().getName());
    }

    /** 被监控的方法抛出异常后 */
    @AfterThrowing(value = "controllerPoint()", throwing = "e")
    public void cutThrowing(JoinPoint joinPoint, Exception e) {
        log.error("[切面功能] [异常] 切入点: {}, 方法所属 Class: {}, 方法名: {}, 异常信息: {}",
                joinPoint.toString(),
                joinPoint.getSignature().getDeclaringType(),
                joinPoint.getSignature().getName(),
                e.getMessage());
    }
}
