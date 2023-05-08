package com.demo.excel.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ConverterUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Excel Listener 模板, 用于导入数据, 不指定 ExcelClass
 * <br> 1. 可以被 extends
 * <br> 2. 不能被 Spring 管理(@Resource 注解不生效), 用到 Spring 需要用构造方法传参
 * <br> 4. 导入完成的数据保存于 {@link #validList}
 *
 * @author Song gh on 2023/02/28.
 */
@Slf4j
@Getter
public class ListenerNoModel implements ReadListener<Map<Integer, String>> {

// ------------------------------ 常量 ------------------------------
    /** 无效的 head 行数上限 */
    protected Integer maxInvalidHeadRowNum = 0;
    /** 当前无效的 head 行数 */
    protected Integer currentInvalidHeadRowNum = 0;
    /** 标准 head 行数 */
    protected Integer headRowNum = null;
    /** head 校验规则, {@link ProtectedConstants} */
    @Setter
    protected Integer headRules = ProtectedConstants.HEAD_RULES_STRICTLY_CONTAINS;
    /** 校准后 head 是否有效 */
    protected Boolean validHead = false;

// ------------------------------ 变量 ------------------------------
    /** 中英列名对照, Map(中文, 英文) */
    @Setter
    protected Map<String, String> cnToEnHeadNameMap;
    /** Excel 读取的列名(含序号) */
    protected Map<Integer, String> indexedCnHeadMap = new HashMap<>();
    /** 有效数据暂存, Excel 类 */
    protected List<Map<String, Object>> validList = new LinkedList<>();
    /** 导入失败的数据, 非 override 不生效 */
    protected List<List<Object>> invalidList = new LinkedList<>();

// ------------------------------ 构造 ------------------------------

    /**
     * 构造: 附带中英列名对照
     *
     * @param cnToEnHeadNameMap Map(中文, 英文)
     */
    public ListenerNoModel(Map<String, String> cnToEnHeadNameMap) {
        this.cnToEnHeadNameMap = cnToEnHeadNameMap == null ? new HashMap<>() : cnToEnHeadNameMap;
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
            for (int index : excelLine.keySet()) {
                currDataMap.put(indexedCnHeadMap.get(index), excelLine.get(index));
            }
        } else {
            // 存在中英列名转换时
            for (int index : excelLine.keySet()) {
                currDataMap.put(cnToEnHeadNameMap.get(indexedCnHeadMap.get(index)), excelLine.get(index));
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
            for (int i = 0; i <indexedCnHeadMap.size(); i++) {
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
        if (Objects.equals(headRules, ProtectedConstants.HEAD_RULES_CONTAINS)) {
            // 存在有效字段即可
            for (String head : readHeadList) {
                // head 有效
                if (validHeadNameSet.contains(head)) {
                    validHead = true;
                    this.indexedCnHeadMap = readHeadMap;
                    return;
                }
            }
        } else if (Objects.equals(headRules, ProtectedConstants.HEAD_RULES_STRICTLY_CONTAINS)) {
            // 存在有效字段, 且没有无效字段
            boolean tempValid = false;
            for (String head : readHeadList) {
                if (validHeadNameSet.contains(head)) {
                    tempValid = true;
                } else {
                    tempValid = false;
                    break;
                }
            }
            if (tempValid) {
                validHead = true;
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
            headRowNum += maxInvalidHeadRowNum;
            validHead = false;
        } else {
            validHead = true;
            this.indexedCnHeadMap = readHeadMap;
        }
    }
}
