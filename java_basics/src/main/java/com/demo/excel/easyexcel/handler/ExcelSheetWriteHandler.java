package com.demo.excel.easyexcel.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.demo.excel.easyexcel.constants.ExcelConstants;
import com.demo.excel.easyexcel.pojo.ExcelCascadeOption;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Excel 表单配置, 与工具类 EasyExcelUtil 配合使用
 * <br> 功能支持: 配置下拉框, 冻结 head
 *
 * @author Song gh
 * @version 2024/1/30
 */
public class ExcelSheetWriteHandler implements SheetWriteHandler {

    /** head 行数(多层 head 取最下方) */
    private final Integer headRowNum;

    /** 动态下拉框, Map(列, 选项) */
    private final Map<Integer, String[]> indexedDynamicMenuMap;

    /** 联动下拉框位置, Map(组名, 列) */
    private final Map<String, List<Integer>> cascadeMenuIndexMap;

    /** 联动下拉框, Map(组名, 选项) */
    private final Map<String, List<ExcelCascadeOption>> cascadeMenuMap;

    /**
     * 构造
     *
     * @param headRowNum            标题行的数量, 配置单元格时需要跳过
     * @param indexedDynamicMenuMap 动态下拉框
     * @param cascadeMenuMap        联动下拉框
     * @param cascadeMenuIndexMap   联动下拉框位置
     */
    public ExcelSheetWriteHandler(Integer headRowNum, Map<Integer, String[]> indexedDynamicMenuMap,
                                  Map<String, List<Integer>> cascadeMenuIndexMap, Map<String, List<ExcelCascadeOption>> cascadeMenuMap) {
        // 记录 head 行数
        this.headRowNum = 1 + headRowNum;
        // 下拉框
        this.indexedDynamicMenuMap = indexedDynamicMenuMap;
        this.cascadeMenuIndexMap = cascadeMenuIndexMap;
        this.cascadeMenuMap = cascadeMenuMap;
    }

    /**
     * 创建 Sheet 后, 对单元格进行操作
     * <pre>
     * 1. 配置下拉框 </pre>
     */
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();

        // 冻结表头
        sheet.createFreezePane(0, headRowNum);

        // 配置下拉框
        boolean needDropDownMenu = indexedDynamicMenuMap != null && !indexedDynamicMenuMap.isEmpty();
        if (needDropDownMenu) {
            // 添加格式约束
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            // 下拉框格式: index 为存在下拉框的单元格序号, options 为下拉框内容
            indexedDynamicMenuMap.forEach((index, options) -> {
                // 下拉列表约束数据
                DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(options);
                // 设置下拉单元格的首行, 末行, 首列, 末列
                CellRangeAddressList addressList = new CellRangeAddressList(headRowNum, 65536, index, index);
                setValidation(sheet, validationHelper, constraint, addressList);
            });
        }

        // 配置联动下拉框
        setUpCascadeMenu(writeWorkbookHolder, sheet);
    }

    /** 配置联动下拉框 */
    private void setUpCascadeMenu(WriteWorkbookHolder writeWorkbookHolder, Sheet sheet) {
        for (Map.Entry<String, List<ExcelCascadeOption>> cascadeMenu : cascadeMenuMap.entrySet()) {
            String cascadeGroupName = cascadeMenu.getKey();
            List<ExcelCascadeOption> optionList = cascadeMenu.getValue();

            // 创建一个 sheet 用来存放联动下拉框数据, 并设置为隐藏
            Workbook workBook = writeWorkbookHolder.getWorkbook();
            String cascadeSheetName = ExcelConstants.DEFAULT_CASCADE_SHEET_NAME + System.currentTimeMillis();
            Sheet cascadeSheet = workBook.createSheet(cascadeSheetName);
            workBook.setSheetHidden(workBook.getSheetIndex(cascadeSheet), true);

            // 逐行写入具体数据, 每行第一个单元格为父级的值, 后续为子选项
            // 首行没有父级, 需要特殊处理
            Row cascadeSheetRow = cascadeSheet.createRow(0);
            for (int i = 0; i < optionList.size(); i++) {
                cascadeSheetRow.createCell(i).setCellValue(optionList.get(i).getName());
            }

            // 配置联动下拉框首列
            List<Integer> indexList = cascadeMenuIndexMap.get(cascadeGroupName);
            int firstColIndex = indexList.get(0);
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            CellRangeAddressList cascadeRangeList = new CellRangeAddressList(headRowNum, 65536, firstColIndex, firstColIndex);
            String mainEndCol = colIndexToStr(optionList.size());
            DataValidationConstraint mainFormula = validationHelper.createFormulaListConstraint("=" + cascadeSheetName + "!$A$1:$" + mainEndCol + "$1");
            setValidation(sheet, validationHelper, mainFormula, cascadeRangeList);

            // 配置联动下拉框后续列
            // "INDIRECT($A$" + 2 + ")" 表示规则数据会从名称管理器中获取 key 与单元格 A2 值相同的数据: 如果A2是省, 那么此处就是省下面的市
            AtomicInteger cascadeSheetRowId = new AtomicInteger(1);
            dealWithChildLists(workBook, cascadeSheet, optionList, cascadeSheetRowId);
            for (int i = 1; i < indexList.size(); i++) {
                Integer currColIndex = indexList.get(i);
                int parentColIndex = indexList.get(i - 1) + 1;
                String parentCol = "$" + colIndexToStr(parentColIndex) + "$";
                CellRangeAddressList rangeAddressList = new CellRangeAddressList(headRowNum, 65536, currColIndex, currColIndex);
                DataValidationConstraint formula = validationHelper.createFormulaListConstraint("INDIRECT(" + parentCol + (headRowNum + 1) + ")");
                setValidation(sheet, validationHelper, formula, rangeAddressList);
            }

        }
    }

    /** 列名 int 转 str, 用于后续单元格引用 */
    private static String colIndexToStr(int column) {
        if (column <= 0) {
            return null;
        }
        StringBuilder columnStr = new StringBuilder();
        column--;
        do {
            if (columnStr.length() > 0) {
                column--;
            }
            columnStr.insert(0, ((char) (column % 26 + 'A')));
            column = (column - column % 26) / 26;
        } while (column > 0);
        return columnStr.toString();
    }

    /**
     * 设置验证规则, 阻止输入下拉选项以外的值
     *
     * @param sheet            当前页
     * @param validationHelper 验证器
     * @param constraint       数据范围
     * @param rangeList        单元格范围
     */
    private static void setValidation(Sheet sheet, DataValidationHelper validationHelper, DataValidationConstraint constraint, CellRangeAddressList rangeList) {
        DataValidation validation = validationHelper.createValidation(constraint, rangeList);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(true);
        validation.createErrorBox("提示", "请选择下拉框内的数据");
        sheet.addValidationData(validation);
    }

    // 处理子选项, 添加名称管理器
    private void dealWithChildLists(Workbook book, Sheet hideSheet, List<ExcelCascadeOption> cascadeOptionList, AtomicInteger rowId) {
        Optional.ofNullable(cascadeOptionList).ifPresent(l -> l.forEach(cascadeOption -> {
            // 获取子选项
            List<ExcelCascadeOption> childList = cascadeOption.getChildList();
            if (!childList.isEmpty()) {
                Row row = hideSheet.createRow(rowId.getAndIncrement());
                row.createCell(0).setCellValue(cascadeOption.getName());
                IntStream.range(0, childList.size()).forEach(c -> row.createCell(c + 1).setCellValue(childList.get(c).getName()));
                // 添加名称管理器
                String endCol = colIndexToStr(1 + childList.size());
                String range = "$B$" + rowId.get() + ":$" + endCol + "$" + rowId.get();

                Name name = book.createName();
                name.setNameName(cascadeOption.getName());
                name.setRefersToFormula(hideSheet.getSheetName() + "!" + range);
                // 处理嵌套子选项
                dealWithChildLists(book, hideSheet, childList, rowId);
            }
        }));
    }

    /** 获取联动下拉框深度 */
    private int getMaxLevel(List<ExcelCascadeOption> nameCascadeList, int preLevel) {
        int curLevel = preLevel + 1;
        int maxLevel = curLevel;
        for (ExcelCascadeOption excelCascadeOption : nameCascadeList) {
            List<ExcelCascadeOption> childList = excelCascadeOption.getChildList();
            if (!childList.isEmpty()) {
                int level = getMaxLevel(childList, curLevel);
                maxLevel = Math.max(level, maxLevel);
            }
        }
        return maxLevel;
    }
}

