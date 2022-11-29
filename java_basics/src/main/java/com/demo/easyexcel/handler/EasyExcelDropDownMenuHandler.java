package com.demo.easyexcel.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.Map;

/**
 * Excel 下拉框配置, 与工具类 EasyExcelUtil 配合使用
 *
 * @author Song gh on 2022/7/12.
 */
public class EasyExcelDropDownMenuHandler implements SheetWriteHandler {

    /** 配置下拉框时跳过前(包括表头在内) x 行 */
    private final Integer skipRowNum;

    /** 动态下拉框, Map(columnIndex, options) */
    private final Map<Integer, String[]> dropDownMap;

    /** Constructor */
    public EasyExcelDropDownMenuHandler(Map<Integer, String[]> dropDownMap) {
        this.dropDownMap = dropDownMap;
        // 表头不配置下拉框
        this.skipRowNum = 1;
    }

    /** Constructor: 配置下拉框时, 除表头外额外跳过前 x 行 */
    public EasyExcelDropDownMenuHandler(Map<Integer, String[]> dropDownMap, Integer skipRowNum) {
        this.dropDownMap = dropDownMap;
        // 表头不配置下拉框
        this.skipRowNum = 1 + skipRowNum;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 无需配置下拉框
        if (dropDownMap == null || dropDownMap.isEmpty()) {
            return;
        }

        // 这里可以对cell进行任何操作
        Sheet sheet = writeSheetHolder.getSheet();

        // 添加格式约束
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        // 下拉框格式: index 为存在下拉框的单元格序号, options 为下拉框内容
        dropDownMap.forEach((index, options) -> {
            // 下拉列表约束数据
            DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(options);
            // 设置下拉单元格的首行, 末行, 首列, 末列
            CellRangeAddressList rangeList = new CellRangeAddressList(skipRowNum, 65536, index, index);
            // 设置约束
            DataValidation validation = validationHelper.createValidation(constraint, rangeList);
            // 阻止输入非下拉选项的值
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.setShowErrorBox(true);
            validation.setSuppressDropDownArrow(true);
            validation.createErrorBox("提示", "请选择下拉框内的数据");
            sheet.addValidationData(validation);
        });
    }
}

