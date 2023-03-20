package com.demo.easyexcel.util.listner;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ConverterUtils;
import com.demo.easyexcel.util.ValidationUtil;
import com.demo.easyexcel.util.constants.EasyExcelConstants;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateEntity;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateExcelVo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Excel 导入模板类
 * <br> 1. 允许被 extends
 * <br> 2. 不能被 Spring 管理(@Resource 注解不生效), 用到 Spring 需要用构造方法传参
 * <br> 3. 保证 Excel 类 extends {@link EasyExcelTemplateExcelVo}; Entity 类 extends {@link EasyExcelTemplateEntity}
 * <br> 4. 导入完成的数据根据 {@link #autoConvert}, 存入 {@link #validEntityList} 或 {@link #validExcelList}
 *
 * @author Song gh on 2023/02/28.
 */
@Slf4j
@Getter
public class EasyExcelTemplateListener<T extends EasyExcelTemplateExcelVo, U extends EasyExcelTemplateEntity> implements ReadListener<T> {

    // ------------------------------ 常量 ------------------------------
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
    /** 有效数据暂存, Entity 类 */
    protected List<U> validEntityList;
    /** 有效数据暂存, Excel 类 */
    protected List<T> validExcelList;
    /** 导入失败的数据 */
    protected List<T> invalidList;

    /**
     * 是否将 Excel 转换为对应 Entity:
     * true = 数据存入 {@link #validEntityList},
     * false = 数据存入 {@link #validExcelList}
     */
    protected Boolean autoConvert;
    /** Entity 类, 仅在 autoConvert = true 时使用 */
    Class<U> entityClass;
    /** Excel 类 */
    Class<T> excelClass;

    // ------------------------------ 构造 ------------------------------

    /** 构造: 默认 */
    public EasyExcelTemplateListener(Class<T> excelClass) {
        // 常量
        this.maxInvalidHeadRowNum = 0;
        this.currentInvalidHeadRowNum = 0;
        this.validHead = false;
        this.headRules = EasyExcelConstants.HEAD_RULES_CONTAINS;
        this.headRowNum = null;
        // 变量
        this.validEntityList = new LinkedList<>();
        this.validExcelList = new LinkedList<>();
        this.invalidList = new LinkedList<>();
        this.autoConvert = false;
        this.excelClass = excelClass;

        log.info("导入 Excel 开始: " + excelClass.getName());
    }

    /** 构造: Excel 转换为 Entity */
    public EasyExcelTemplateListener(Class<T> excelClass, Class<U> entityClass) {
        // 常量
        this.maxInvalidHeadRowNum = 0;
        this.currentInvalidHeadRowNum = 0;
        this.validHead = false;
        this.headRules = EasyExcelConstants.HEAD_RULES_CONTAINS;
        this.headRowNum = null;
        // 变量
        this.validEntityList = new LinkedList<>();
        this.validExcelList = new LinkedList<>();
        this.invalidList = new LinkedList<>();
        this.autoConvert = true;
        this.excelClass = excelClass;
        this.entityClass = entityClass;

        log.info("导入 Excel 并自动转换开始: " + excelClass.getName());
    }

    // ------------------------------ 可 Override ------------------------------

    /** 循环处理单行数据 */
    @Override
    public void invoke(T excelLine, AnalysisContext context) {
        // 基础校验
        String errorMessage = ValidationUtil.validate(excelLine);
        // 记录报错数据
        if (StringUtils.isNotBlank(errorMessage)) {
            excelLine.setDefaultErrorMessage(errorMessage);
            invalidList.add(excelLine);
            return;
        }

        // 处理通过基础校验的数据
        if (autoConvert) {
            // Excel 转换为 Entity
            try {
                U currEntity = entityClass.newInstance();
                BeanUtils.copyProperties(excelLine, currEntity);
                // override 此方法可进行自定义参数设置
                currEntity.setParamsAfterCopy(excelLine);
                validEntityList.add(currEntity);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("转换 Excel 失败, 请检查 " + excelClass.getName() + " 与 " + entityClass.getName(), e);
            }
        } else {
            // 保持 Excel, 不需要转换为 Entity
            validExcelList.add(excelLine);
        }
    }

    /** 处理完成后收尾步骤 */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 校验, 有报错则整批不存入
        if (!invalidList.isEmpty()) {
            log.info("导入 Excel 失败: " + excelClass.getName());
            return;
        }
        log.info(" 导入 Excel 数据校验完成: " + excelClass.getName());
    }

    // ------------------------------ 不建议 Override ------------------------------

    /**
     * 设置 head 校验规则
     *
     * @see EasyExcelConstants
     */
    public void setHeadRules(Integer headRules) {
        this.headRules = headRules;
    }

    /** 读取 head */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        // 校验
        validFirstLineHead(ConverterUtils.convertToStringMap(headMap, context), context);
    }

    /** head 无效时提前终止 */
    @Override
    public boolean hasNext(AnalysisContext context) {
        return validHead;
    }

    // ------------------------------ Private ------------------------------

    /** head 校验: 当前行是否有效(用于跳过 head 前多余行数) */
    private void validFirstLineHead(Map<Integer, String> headMap, AnalysisContext context) {
        // 标准 head 行数
        if (headRowNum == null) {
            headRowNum = context.readSheetHolder().getHeadRowNumber();
        }
        // 检验当前行 head 是否有效
        List<String> headList = new ArrayList<>(headMap.values());
        Set<String> headNameSet = getHeadNameSet(excelClass);
        // 根据设定规则进行校验
        if (Objects.equals(headRules, EasyExcelConstants.HEAD_RULES_CONTAINS)) {
            // 存在有效字段即可
            for (String head : headList) {
                // head 有效
                if (headNameSet.contains(head)) {
                    validHead = true;
                    return;
                }
            }
        } else if (Objects.equals(headRules, EasyExcelConstants.HEAD_RULES_STRICTLY_CONTAINS)) {
            // 存在有效字段, 且没有无效字段
            boolean tempValid = false;
            for (String head : headList) {
                if (headNameSet.contains(head)) {
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
        // 超上限, 校准并重置
        if (currentInvalidHeadRowNum > maxInvalidHeadRowNum) {
            maxInvalidHeadRowNum++;
            currentInvalidHeadRowNum = 0;
            validHead = false;
            headRowNum += maxInvalidHeadRowNum;
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
}
