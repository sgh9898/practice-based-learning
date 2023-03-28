package com.demo.excel.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ConverterUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Excel Listener 模板, 用于导入数据
 * <br> 1. 可以被 extends
 * <br> 2. 不能被 Spring 管理(@Resource 注解不生效), 用到 Spring 需要用构造方法传参
 * <br> 4. 导入完成的数据保存于 {@link #validList}
 *
 * @author Song gh on 2023/02/28.
 */
@Slf4j
@Getter
public class Listener<T> implements ReadListener<T> {

// ------------------------------ 常量 ------------------------------
    /** 校验 */
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    /** 无效的 head 行数上限 */
    protected Integer maxInvalidHeadRowNum;
    /** 当前无效的 head 行数 */
    protected Integer currentInvalidHeadRowNum;
    /** 校准后 head 是否有效 */
    protected Boolean validHead;
    /** head 校验规则 */
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
    public Listener(Class<T> excelClass) {
        // 常量
        this.maxInvalidHeadRowNum = 0;
        this.currentInvalidHeadRowNum = 0;
        this.validHead = false;
        this.headRules = Constants.HEAD_RULES_CONTAINS;
        this.headRowNum = null;
        // 变量
        this.validList = new LinkedList<>();
        this.invalidList = new LinkedList<>();
        this.excelClass = excelClass;

        log.info("导入 Excel 开始: " + excelClass.getName());
    }

// ------------------------------ 可 Override ------------------------------

    /** 循环处理单行数据 */
    @Override
    public void invoke(T excelLine, AnalysisContext context) {
        // 基础校验
        String errorMessage = validate(excelLine);
        // 记录报错数据
        if (StringUtils.isNotBlank(errorMessage)) {
            if (excelLine instanceof TemplateExcel) {
                ((TemplateExcel) excelLine).setDefaultExcelErrorMessage(errorMessage);
            } else {
                for (Field currField : excelLine.getClass().getDeclaredFields()) {
                    if (currField.getName().equals(Constants.DEFAULT_ERROR_PARAM)) {
                        try {
                            currField.set(excelLine, errorMessage);
                        } catch (IllegalAccessException e) {
                            log.error("设置 Excel 导入错误信息失败", e);
                        }
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
        // 校验, 有报错则整批不存入
        if (!invalidList.isEmpty()) {
            log.info("导入 Excel 失败: " + excelClass.getName());
            return;
        }
        log.info("导入 Excel 完成: " + excelClass.getName());
    }

    /**
     * 设置 head 校验规则
     *
     * @see Constants
     */
    public void setHeadRules(Integer headRules) {
        this.headRules = headRules;
    }

    /** 读取 head */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        // 校验
        checkHeadCurrentLine(ConverterUtils.convertToStringMap(headMap, context), context);
    }

// ------------------------------ Private ------------------------------

    /** head 无效时提前终止 */
    @Override
    public boolean hasNext(AnalysisContext context) {
        return validHead;
    }

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
        if (Objects.equals(headRules, Constants.HEAD_RULES_CONTAINS)) {
            // 存在有效字段即可
            for (String currHead : readHeadList) {
                // head 有效
                if (validHeadNameSet.contains(currHead)) {
                    validHead = true;
                    return;
                }
            }
        } else if (Objects.equals(headRules, Constants.HEAD_RULES_STRICTLY_CONTAINS)) {
            // 存在有效字段, 且没有无效字段
            boolean tempValid = false;
            for (String currHead : readHeadList) {
                if (validHeadNameSet.contains(currHead)) {
                    tempValid = true;
                } else {
                    tempValid = false;
                    break;
                }
            }
            if (tempValid) {
                validHead = true;
                return;
            }
        }

        // 当前行 head 无效
        currentInvalidHeadRowNum++;
        // 未读取到有效 head, 增加 head 行数, 重新读取文件
        if (currentInvalidHeadRowNum > maxInvalidHeadRowNum) {
            maxInvalidHeadRowNum++;
            currentInvalidHeadRowNum = 0;
            headRowNum += maxInvalidHeadRowNum;
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
    private static String validate(Object entity) {
        Set<ConstraintViolation<Object>> violationSet = validator.validate(entity);
        for (ConstraintViolation<Object> violation : violationSet) {
            return violation.getMessage();
        }
        return null;
    }
}
