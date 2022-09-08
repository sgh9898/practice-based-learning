package com.demo.easyexcel.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;
import java.util.Map;

/**
 * Excel 导出配置, 与工具类 EasyExcelUtil 配合使用
 *
 * @author Song gh on 2022/7/12.
 */
public class EasyExcelWriteHandler implements SheetWriteHandler {

    private final List<Integer> columnSizeList;
    private final Map<Integer, String[]> dropDownMap;
    private final String note;
    private final Integer lastColIndex;

    /**
     * constructor
     *
     * @param columnSizeList 列宽
     * @param dropDownMap    动态下拉框
     * @param note           说明
     * @param lastColIndex   最后一列的 index
     */
    public EasyExcelWriteHandler(List<Integer> columnSizeList, Map<Integer, String[]> dropDownMap, String note, Integer lastColIndex) {
        this.columnSizeList = columnSizeList;
        this.dropDownMap = dropDownMap;
        this.note = StringUtils.isBlank(note) ? null : note;
        this.lastColIndex = lastColIndex == null ? 0 : lastColIndex;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 下拉框为空不进行操作
        if (dropDownMap == null || dropDownMap.isEmpty()) {
            return;
        }
        // 这里可以对cell进行任何操作
        Sheet sheet = writeSheetHolder.getSheet();

        // 添加格式约束
        DataValidationHelper helper = sheet.getDataValidationHelper();
        // 下拉框格式: index 为存在下拉框的单元格序号, options 为下拉框内容
        dropDownMap.forEach((index, options) -> {
            // 下拉列表约束数据
            DataValidationConstraint constraint = helper.createExplicitListConstraint(options);
            // 设置下拉单元格的首行, 末行, 首列, 末列
            CellRangeAddressList rangeList = new CellRangeAddressList(1, 65536, index, index);
            // 设置约束
            DataValidation validation = helper.createValidation(constraint, rangeList);
            // 阻止输入非下拉选项的值
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.setShowErrorBox(true);
            validation.setSuppressDropDownArrow(true);
            validation.createErrorBox("提示", "请选择下拉框内的数据");
            sheet.addValidationData(validation);
        });

        // 添加首行说明
        if ((note != null)) {
            // 需要合并单元格
            if (lastColIndex - 1 > 0) {
                CellRangeAddress mergedRegion = new CellRangeAddress(1, 1, 0, lastColIndex - 1);
                sheet.addMergedRegion(mergedRegion);
            }
            // 自适应列宽
            for (int i = 0; i < columnSizeList.size(); i++) {
                sheet.setColumnWidth(i, (int) (columnSizeList.get(i) * 3.5 * 256));
            }
            Row row = sheet.createRow(0);
            row.setHeightInPoints(50);
        }
    }
}

