package com.demo.excel.easyexcel;

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
 * Excel 表单配置, 与工具类 EasyExcelUtil 配合使用
 * <br> 1.配置下拉框
 * <br> 2.冻结 head
 *
 * @author Song gh on 2022/7/12.
 */
class ZippedSheetWriteHandler implements SheetWriteHandler {

    /** head 行数(多层 head 取最下方) */
    private final Integer headRowNum;

    /** 动态下拉框, Map(columnIndex, options) */
    private final Map<Integer, String[]> dropDownMap;

    /** 构造: 默认 */
    public ZippedSheetWriteHandler(Map<Integer, String[]> dropDownMap) {
        this.dropDownMap = dropDownMap;
        // head 不配置下拉框
        this.headRowNum = 1;
    }

    /** 构造: 配置下拉框时, 除 head 外额外跳过前 x 行 */
    public ZippedSheetWriteHandler(Map<Integer, String[]> dropDownMap, Integer headRowNum) {
        this.dropDownMap = dropDownMap;
        // head 不配置下拉框
        this.headRowNum = 1 + headRowNum;
    }

    /** 可以对cell进行任何操作 */
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();

        // 冻结表头
        sheet.createFreezePane(0, headRowNum);

        // 配置下拉框
        boolean needDropDownMenu = dropDownMap != null && !dropDownMap.isEmpty();
        if (needDropDownMenu) {
            // 添加格式约束
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            // 下拉框格式: index 为存在下拉框的单元格序号, options 为下拉框内容
            dropDownMap.forEach((index, options) -> {
                // 下拉列表约束数据
                DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(options);
                // 设置下拉单元格的首行, 末行, 首列, 末列
                CellRangeAddressList rangeList = new CellRangeAddressList(headRowNum, 65536, index, index);
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
}

