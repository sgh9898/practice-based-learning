package com.demo.excel.easyexcel;

import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;

/**
 * Excel 自适应行高, 与工具类 EasyExcelUtil 配合使用
 *
 * @author Song gh on 2022/10/18.
 */
class ProtectedHandlerRowHeight extends AbstractRowHeightStyleStrategy {

    /** string 单行高度 */
    private static final Integer LINE_HEIGHT = 370;

    @Override
    protected void setHeadColumnHeight(Row row, int relativeRowIndex) {
    }

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
            // if (cell.getCellTypeEnum() == CellType.STRING) {
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