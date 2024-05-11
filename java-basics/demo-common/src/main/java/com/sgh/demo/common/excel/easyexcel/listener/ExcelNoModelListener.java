package com.sgh.demo.common.excel.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ConverterUtils;
import com.sgh.demo.common.excel.easyexcel.constants.ExcelConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * Excel Listener 模板, 不指定 ExcelClass
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
public class ExcelNoModelListener implements ReadListener<Map<Integer, String>> {

// ------------------------------ 常量 ------------------------------
    /** 无效的 head 行数上限 */
    protected Integer maxInvalidHeadRowNum = 0;
    /** 当前无效的 head 行数 */
    protected Integer currentInvalidHeadRowNum = 0;
    /** 标准 head 行数 */
    protected Integer headRowNum = null;
    /** head 校验规则, {@link ExcelConstants} */
    @Setter
    protected Integer headRules = ExcelConstants.HEAD_RULES_CONTAINS;
    /** 校准后 head 是否有效 */
    protected Boolean validHead = false;

// ------------------------------ 变量 ------------------------------
    /** 中英列名对照, Map(中文, 英文) */
    @Setter
    @NonNull
    protected Map<String, String> cnToEnHeadNameMap;
    /** Excel 读取的列名(含序号) */
    protected Map<Integer, String> indexedCnHeadMap = new HashMap<>();
    /** 有效数据暂存, Excel 类 */
    protected List<Map<String, Object>> validList = new LinkedList<>();
    /** 导入失败的数据, 默认仅文件无内容时报错, 可以 override 对应的校验方法 */
    protected List<List<Object>> invalidList = new LinkedList<>();

// ------------------------------ 构造 ------------------------------

    /**
     * 构造: 附带中英列名对照
     *
     * @param cnToEnHeadNameMap Map(中文, 英文)
     * @param headRules         校验规则 {@link ExcelConstants}
     */
    public ExcelNoModelListener(@Nullable Map<String, String> cnToEnHeadNameMap, Integer headRules) {
        this.cnToEnHeadNameMap = cnToEnHeadNameMap == null ? new HashMap<>() : cnToEnHeadNameMap;
        if (headRules != null) {
            this.headRules = headRules;
        }
        log.info("不指定 ExcelClass 读取 Excel 开始");
    }

// ------------------------------ 可 Override ------------------------------

    /** 循环处理单行数据 */
    @Override
    public void invoke(Map<Integer, String> excelLine, AnalysisContext context) {
        // 格式转换
        Map<String, Object> currDataMap = new HashMap<>();
        if (cnToEnHeadNameMap.isEmpty()) {
            // 不存在中英列名转换时
            for (Map.Entry<Integer, String> currLine : excelLine.entrySet()) {
                currDataMap.put(indexedCnHeadMap.get(currLine.getKey()), currLine.getValue());
            }
        } else {
            // 存在中英列名转换时
            for (Map.Entry<Integer, String> currLine : excelLine.entrySet()) {
                String columnEnName = cnToEnHeadNameMap.get(indexedCnHeadMap.get(currLine.getKey()));
                if (StringUtils.isNotBlank(columnEnName)) {
                    currDataMap.put(columnEnName, currLine.getValue());
                }
            }
        }
        validList.add(currDataMap);
    }

    /** 处理完成后收尾步骤 */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 校验
        if (!invalidList.isEmpty()) {
            log.info("不指定 ExcelClass 读取 Excel 完成, 存在错误信息");
            return;
        } else if (validList.isEmpty()) {
            List<Object> tempInnerList = new LinkedList<>();
            for (int i = 0; i < indexedCnHeadMap.size(); i++) {
                tempInnerList.add(null);
            }
            tempInnerList.add("文件内容为空");
            invalidList.add(tempInnerList);
            log.info("不指定 ExcelClass 读取 Excel 完成, 文件为空");
            return;
        }
        log.info("不指定 ExcelClass 读取 Excel 完成, 均通过初步校验");
    }

// ------------------------------ 不建议 Override ------------------------------

    /** 读取 head */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
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

        // 未设定 head 时不需要校验
        if (cnToEnHeadNameMap.isEmpty()) {
            validHead = true;
            this.indexedCnHeadMap = new HashMap<>(readHeadMap);
            return;
        }

        // 检验当前行 head 是否有效
        List<String> readHeadList = new ArrayList<>(readHeadMap.values());
        Set<String> validHeadNameSet = new HashSet<>(cnToEnHeadNameMap.keySet());
        // 根据设定规则进行校验
        if (Objects.equals(headRules, ExcelConstants.HEAD_RULES_CONTAINS)) {
            // 存在有效字段即可
            for (String head : readHeadList) {
                // head 有效
                if (validHeadNameSet.contains(head)) {
                    validHead = true;
                    this.indexedCnHeadMap = readHeadMap;
                    return;
                }
            }
        } else if (Objects.equals(headRules, ExcelConstants.HEAD_RULES_STRICTLY_CONTAINS)) {
            // 存在有效字段, 且没有无效字段
            validHead = true;
            for (String head : readHeadList) {
                if (!validHeadNameSet.contains(head)) {
                    validHead = false;
                    break;
                }
            }
            if (Boolean.TRUE.equals(validHead)) {
                this.indexedCnHeadMap = readHeadMap;
                return;
            }
        }
        // 当前行 head 无效
        currentInvalidHeadRowNum++;
        // 超上限, 校准并重置
        if (currentInvalidHeadRowNum > maxInvalidHeadRowNum) {
            maxInvalidHeadRowNum++;
            currentInvalidHeadRowNum = 0;
            headRowNum++;
            validHead = false;
        } else {
            validHead = true;
            this.indexedCnHeadMap = readHeadMap;
        }
    }
}
