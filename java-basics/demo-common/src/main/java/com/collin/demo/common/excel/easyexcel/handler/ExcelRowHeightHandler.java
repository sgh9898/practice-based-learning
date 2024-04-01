package com.collin.demo.common.excel.easyexcel.handler;

import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;

/**
 * Excel 自适应行高, 与工具类 EasyExcelUtil 配合使用
 *
 * @author Song gh
 * @version 2024/1/30
 */
public class ExcelRowHeightHandler extends AbstractRowHeightStyleStrategy {

    /** string 单行高度 */
    private static final Integer LINE_HEIGHT = 370;

    /** 配置 head 高度 */
    @Override
    protected void setHeadColumnHeight(Row row, int relativeRowIndex) {
        // 需要时可自行配置
    }

    /** 配置单元格高度 */
    @Override
    protected void setContentColumnHeight(Row row, int relativeRowIndex) {
        Iterator<Cell> cellIterator = row.cellIterator();
        if (!cellIterator.hasNext()) {
            return;
        }

        // 调整行高, 仅对 string 生效
        int maxHeight = 1;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            // 根据 maven 依赖版本可能需要更换
//             if (cell.getCellTypeEnum() == CellType.STRING) {
            if (cell.getCellType() == CellType.STRING) {
                // 获取内容本身的换行
                String cellStr = cell.getStringCellValue();
                String[] lines = cellStr.split("\r\n|\r|\n");
                maxHeight = Math.max(maxHeight, lines.length);
            }
        }
        row.setHeight((short) (maxHeight * LINE_HEIGHT));
    }
}