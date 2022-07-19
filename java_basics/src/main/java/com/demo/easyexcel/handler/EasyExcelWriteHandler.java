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
 * Excel 导出配置, 与工具类 EasyExcelUtil 配合使用
 *
 * @author Song gh on 2022/7/12.
 */
public class EasyExcelWriteHandler implements SheetWriteHandler {

    private final Map<Integer, String[]> dropDownMap;

    public EasyExcelWriteHandler(Map<Integer, String[]> dropDownMap) {
        this.dropDownMap = dropDownMap;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 这里可以对cell进行任何操作
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        // index 为存在下拉框的单元格序号, options 为下拉框内容
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
        // 设置验证生效的范围(起始行, 结束行, 起始列, 结束列)
        CellRangeAddressList addressList = new CellRangeAddressList(1, 65536, 0, 20);
        // 设置验证方式(Date(1990, 1, 1)是excel的日期函数, 能成功解析, 写成"1990-01-01"解析失败)
        // 需要其他日期格式, 修改第四个参数"yyyy-MM-dd", eg："yyyy-MM-dd HH:mm:ss"
        DataValidationConstraint constraint = helper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "Date(1900, 1, 1)", "Date(9999, 12, 31)", "yyyy-MM-dd");
        // 创建验证对象
        DataValidation dataValidation = helper.createValidation(constraint, addressList);
        // 错误提示信息
        dataValidation.createErrorBox("提示", "请输入[yyyy-MM-dd]格式日期, 范围:[1990-01-01,9999-12-31]");
        dataValidation.setShowErrorBox(true);
        // 验证和工作簿绑定
        sheet.addValidationData(dataValidation);
    }
}

