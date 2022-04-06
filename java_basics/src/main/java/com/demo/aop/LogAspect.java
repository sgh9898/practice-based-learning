package com.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 使用 aop 切面对已有日志功能进行扩充
 * <br> 需要 @Aspect, @Component 标注
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
    @Pointcut("execution(public * com.demo.controller.ExcelController.downloadSqlTemplate())")
    public void controllerPoint() {
    }

    @Before("controllerPoint()")
    public void beforeCp(JoinPoint joinPoint) {
        log.info(joinPoint.toString());
        log.info("Controller 切面, 调用对象: {}, 方法: {}",
                joinPoint.getSignature().getDeclaringType(),
                joinPoint.getSignature().getName());
    }

    @AfterThrowing(value = "controllerPoint()", throwing = "e")
    public void afterCpThrowing(JoinPoint joinPoint, Exception e) {
        //发生异常之后输出异常信息
        log.error(joinPoint + ",发生异常：" + e.getMessage());
    }
}
