package com.sgh.demo.general.excel.easyexcel.listener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ConverterUtils;
import com.sgh.demo.general.excel.easyexcel.EasyExcelClassTemplate;
import com.sgh.demo.general.excel.easyexcel.constants.ExcelConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Excel Listener 模板
 * <pre>
 * 1. 用于导入数据, 绝大多数情况不需要更改; 仅当必须修改 Listener 时, 可以 extends 本类
 * 2. 不能被 Spring 管理(@Resource 注解不生效), 用到 Spring 需要用构造方法传参
 * 3. 导入完成的数据保存于 {@link #validList} </pre>
 *
 * @author Song gh
 * @version 2024/1/30
 */
@Slf4j
@Getter
public class ExcelListener<T> implements ReadListener<T> {

// ------------------------------ 常量 ------------------------------
    /** 校验 */
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    /** 无效的 head 行数上限 */
    protected Integer maxInvalidHeadRowNum;
    /** 当前无效的 head 行数 */
    protected Integer currentInvalidHeadRowNum;
    /** 校准后 head 是否有效 */
    protected Boolean validHead;
    /** head 校验规则, {@link ExcelConstants} */
    @Setter
    protected Integer headRules;
    /** 标准 head 行数 */
    protected Integer headRowNum;

// ------------------------------ 变量 ------------------------------
    /** 有效数据暂存, Excel 类 */
    protected List<T> validList;
    /** 导入失败的数据 */
    protected List<T> invalidList;
    /** Excel 类 */
    Class<T> excelClass;

// ------------------------------ 构造 ------------------------------

    /** 构造: 默认 */
    public ExcelListener(Class<T> excelClass) {
        // 常量
        this.maxInvalidHeadRowNum = 0;
        this.currentInvalidHeadRowNum = 0;
        this.validHead = false;
        this.headRules = ExcelConstants.HEAD_RULES_CONTAINS;
        this.headRowNum = null;
        // 变量
        this.validList = new LinkedList<>();
        this.invalidList = new LinkedList<>();
        this.excelClass = excelClass;

        log.info("导入 Excel 开始: {}", excelClass.getName());
    }

// ------------------------------ 可 Override ------------------------------

    /** 循环处理单行数据 */
    @Override
    public void invoke(T excelLine, AnalysisContext context) {
        // 基础校验
        String errorMessage = validate(excelLine);
        // 记录报错数据
        if (StringUtils.isNotBlank(errorMessage)) {
            if (excelLine instanceof EasyExcelClassTemplate) {
                ((EasyExcelClassTemplate) excelLine).setDefaultExcelErrorMessage(errorMessage);
            } else {
                for (Field currField : excelLine.getClass().getDeclaredFields()) {
                    if (currField.getName().equals(ExcelConstants.DEFAULT_ERROR_PARAM)) {
                        ReflectionUtils.setField(currField, excelLine, errorMessage);
                    }
                }
            }
            invalidList.add(excelLine);
            return;
        }

        validList.add(excelLine);
    }

// ------------------------------ 不建议 Override ------------------------------

    /** 处理完成后收尾步骤 */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 校验
        if (!invalidList.isEmpty()) {
            log.info("读取 Excel 完成, 存在错误信息, 所属类别: {}", excelClass.getName());
            return;
        } else if (validList.isEmpty()) {
            try {
                T tempExcel = excelClass.getDeclaredConstructor().newInstance();
                if (tempExcel instanceof EasyExcelClassTemplate) {
                    ((EasyExcelClassTemplate) tempExcel).setDefaultExcelErrorMessage("文件内容为空或列名不匹配");
                } else {
                    for (Field currField : tempExcel.getClass().getDeclaredFields()) {
                        if (currField.getName().equals(ExcelConstants.DEFAULT_ERROR_PARAM)) {
                            ReflectionUtils.setField(currField, tempExcel, "文件内容为空或列名不匹配");
                        }
                    }
                }
                invalidList.add(tempExcel);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                log.error("设置 Excel 导入错误信息失败", e);
            }
            log.info("读取 Excel 完成, 文件内容为空或列名不匹配, 所属类别: {}", excelClass.getName());
            return;
        }
        log.info("读取 Excel 完成, 均通过初步校验, 所属类别: {} ", excelClass.getName());
    }

    /** 读取 head */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        // 校验
        checkHeadCurrentLine(ConverterUtils.convertToStringMap(headMap, context), context);
    }

    /** head 无效时提前终止 */
    @Override
    public boolean hasNext(AnalysisContext context) {
        return validHead;
    }

// ------------------------------ Private ------------------------------

    /** head 校验: 当前行是否有效(用于跳过 head 前多余行数) */
    private void checkHeadCurrentLine(Map<Integer, String> readHeadMap, AnalysisContext context) {
        // 初始化标准 head 行数
        if (headRowNum == null) {
            headRowNum = context.readSheetHolder().getHeadRowNumber();
        }

        // 检验当前行 head 是否有效
        List<String> readHeadList = new ArrayList<>(readHeadMap.values());
        Set<String> validHeadNameSet = getHeadNameSet(excelClass);
        // 根据设定规则进行校验
        if (Objects.equals(headRules, ExcelConstants.HEAD_RULES_CONTAINS)) {
            // 存在有效字段即可
            for (String currHead : readHeadList) {
                // head 有效
                if (validHeadNameSet.contains(currHead)) {
                    validHead = true;
                    return;
                }
            }
        } else if (Objects.equals(headRules, ExcelConstants.HEAD_RULES_STRICTLY_CONTAINS)) {
            // 存在有效字段, 且没有无效字段
            boolean tempValid = true;
            for (String currHead : readHeadList) {
                if (!validHeadNameSet.contains(currHead)) {
                    tempValid = false;
                    break;
                }
            }
            validHead = tempValid;
            return;
        }

        // 当前行 head 无效
        currentInvalidHeadRowNum++;
        // 未读取到有效 head, 增加 head 行数, 重新读取文件
        if (currentInvalidHeadRowNum > maxInvalidHeadRowNum) {
            maxInvalidHeadRowNum++;
            currentInvalidHeadRowNum = 0;
            headRowNum++;
            validHead = false;
        } else {
            validHead = true;
        }
    }

    /** 获取指定 Class 全部列名({@link ExcelProperty}) */
    private static Set<String> getHeadNameSet(Class<?> targetClass) {
        // 记录列名, 用于动态列名替换
        Set<String> headNameSet = new HashSet<>();
        for (Field field : targetClass.getDeclaredFields()) {
            ExcelProperty excelAnnotation = field.getAnnotation(ExcelProperty.class);
            if (excelAnnotation != null) {
                headNameSet.addAll(Arrays.asList(excelAnnotation.value()));
            }
        }
        return headNameSet;
    }

    /** 校验实体类: 通过返回 null, 未通过返回报错 */
    @NonNull
    private static String validate(Object entity) {
        Set<ConstraintViolation<Object>> violationSet = validator.validate(entity);
        StringBuilder errorMsg = new StringBuilder();
        for (ConstraintViolation<Object> violation : violationSet) {
            errorMsg.append(violation.getMessage()).append(';');
        }
        return errorMsg.toString();
    }
}
