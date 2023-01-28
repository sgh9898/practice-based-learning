package com.demo.easyexcel.util.handler;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.demo.easyexcel.util.enums.EasyExcelColumnWidthEnums;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel 自适应列宽, 与工具类 EasyExcelUtil 配合使用
 *
 * @author Song gh on 2022/9/27.
 */
@Getter
public class EasyExcelColumnWidthHandler extends AbstractColumnWidthStyleStrategy {

    /** 最大列宽 */
    private static final int MAX_COLUMN_WIDTH = 255;

    /** 列宽总表, Map(sheetIndex, Map(columnIndex, width)) */
    private final Map<Integer, Map<Integer, Integer>> fileColumnWidthMap = MapUtils.newHashMapWithExpectedSize(8);

    /**
     * 列宽选取方式
     * 1. 表头为准 --> 仅使用表头宽度
     * 2. 内容为准 --> 仅使用内容宽度
     */
    private final EasyExcelColumnWidthEnums widthStrategy;

    /** Constructor */
    public EasyExcelColumnWidthHandler() {
        widthStrategy = EasyExcelColumnWidthEnums.COLUMN_WIDTH_DEFAULT;
    }

    /** Constructor: 指定列宽选取方式 */
    public EasyExcelColumnWidthHandler(EasyExcelColumnWidthEnums widthStrategy) {
        if (widthStrategy == EasyExcelColumnWidthEnums.COLUMN_WIDTH_USE_HEAD
                || widthStrategy == EasyExcelColumnWidthEnums.COLUMN_WIDTH_USE_CONTENT) {
            this.widthStrategy = widthStrategy;
        } else {
            this.widthStrategy = EasyExcelColumnWidthEnums.COLUMN_WIDTH_DEFAULT;
        }
    }

    /** 设定列宽(单行, 此方法会被循环调用) */
    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell,
                                  Head head, Integer relativeRowIndex, Boolean isHead) {
        // 排除不为标题且空置的单元格
        boolean needSetWidth = isHead || !CollectionUtils.isEmpty(cellDataList);
        if (!needSetWidth) {
            return;
        }

        // 获取或初始化当前 sheet 列宽数据
        Map<Integer, Integer> sheetColumnWidthMap = fileColumnWidthMap.computeIfAbsent(writeSheetHolder.getSheetNo(), key -> new HashMap<>(16));
        // 计算当前宽度
        Integer columnWidth = dataLength(cellDataList, cell, isHead);
        if (columnWidth < 0) {
            return;
        }
        if (columnWidth > MAX_COLUMN_WIDTH) {
            columnWidth = MAX_COLUMN_WIDTH;
        }
        // 获取当前列宽
        Integer maxColumnWidth = sheetColumnWidthMap.get(cell.getColumnIndex());

        // 更新列宽
        // 使用表头列宽
        if (widthStrategy == EasyExcelColumnWidthEnums.COLUMN_WIDTH_USE_HEAD) {
            if (isHead) {
                // 多行表头仅使用最后一行校准
                sheetColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
            }
        }
        // 使用内容列宽
        else if (widthStrategy == EasyExcelColumnWidthEnums.COLUMN_WIDTH_USE_CONTENT) {
            if (!isHead) {
                if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
                    sheetColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                    writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
                }
            }
        }
        // 默认列宽
        else {
            if (isHead) {
                // 多行表头仅使用最后一行校准
                sheetColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
            } else {
                if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
                    sheetColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                    writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
                }
            }
        }
    }

    /** 计算单元格数据宽度 */
    private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
        // 列名直接返回
        if (isHead) {
            return cell.getStringCellValue().getBytes().length;
        }

        // 数据根据类型计算长度
        WriteCellData<?> cellData = cellDataList.get(0);
        CellDataTypeEnum type = cellData.getType();
        if (type == null) {
            return -1;
        }
        switch (type) {
            case STRING:
                // 考虑换行, 取最长的一行
                String[] lines = cellData.getStringValue().split("\r\n|\r|\n");
                int longestLine = 0;
                for (String line : lines) {
                    longestLine = Math.max(line.getBytes().length, longestLine);
                }
                return longestLine;
            case BOOLEAN:
                return cellData.getBooleanValue().toString().getBytes().length;
            case NUMBER:
                return cellData.getNumberValue().toString().getBytes().length;
            default:
                return -1;
        }
    }
}
