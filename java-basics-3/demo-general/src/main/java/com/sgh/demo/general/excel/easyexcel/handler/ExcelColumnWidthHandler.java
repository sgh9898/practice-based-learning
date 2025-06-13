package com.sgh.demo.general.excel.easyexcel.handler;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * Excel 自适应列宽, 与工具类 EasyExcelUtil 配合使用
 *
 * @author Song gh
 * @version 2024/1/30
 */
@Getter
public class ExcelColumnWidthHandler extends AbstractColumnWidthStyleStrategy {

    /** 最小列宽(字符, 2.54 mm) */
    private static final int MIN_COLUMN_WIDTH = 15;
    /** 最大列宽(字符, 2.54 mm) */
    private static final int MAX_COLUMN_WIDTH = 255;

    /** 列宽总表, Map(sheetIndex, Map(columnIndex, width)) */
    private final Map<Integer, Map<Integer, Integer>> fileColumnWidthMap = MapUtils.newHashMapWithExpectedSize(8);

    /** 不自动调整列宽的列 */
    private final Set<String> doNotChangeWidth = new HashSet<>();

    /**
     * 列宽选取方式
     * 1. head 为准 --> 仅使用 head 宽度
     * 2. 内容为准 --> 仅使用内容宽度
     */
    private final ExcelColWidthStrategy widthStrategy;

    /** Constructor: 指定列宽选取方式 */
    public ExcelColumnWidthHandler(@Nullable ExcelColWidthStrategy widthStrategy) {
        if (widthStrategy == ExcelColWidthStrategy.COL_WIDTH_HEAD || widthStrategy == ExcelColWidthStrategy.COL_WIDTH_CONTENT) {
            this.widthStrategy = widthStrategy;
        } else {
            this.widthStrategy = ExcelColWidthStrategy.COL_WIDTH_DEFAULT;
        }
    }

    /** Constructor: 指定列宽选取方式, 部分固定列宽字段 */
    public ExcelColumnWidthHandler(@Nullable ExcelColWidthStrategy widthStrategy, Set<String> doNotChangeWidth) {
        if (widthStrategy == ExcelColWidthStrategy.COL_WIDTH_HEAD || widthStrategy == ExcelColWidthStrategy.COL_WIDTH_CONTENT) {
            this.widthStrategy = widthStrategy;
        } else {
            this.widthStrategy = ExcelColWidthStrategy.COL_WIDTH_DEFAULT;
        }
        this.doNotChangeWidth.addAll(doNotChangeWidth);
    }

    /** 设定列宽(单行, 此方法会被循环调用) */
    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell,
                                  Head head, Integer relativeRowIndex, Boolean isHead) {
        // 排除手动设置列宽的列
        if (this.doNotChangeWidth.contains(head.getFieldName())) {
            return;
        }
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
        columnWidth = Math.max(MIN_COLUMN_WIDTH, columnWidth);
        columnWidth = Math.min(MAX_COLUMN_WIDTH, columnWidth);

        // 获取当前列宽
        Integer maxColumnWidth = sheetColumnWidthMap.get(cell.getColumnIndex());

        // 更新列宽
        boolean headCells = Boolean.TRUE.equals(isHead);
        boolean columnCells = Boolean.FALSE.equals(isHead) && (maxColumnWidth == null || columnWidth > maxColumnWidth);
        switch (widthStrategy) {
            // 使用 head 列宽
            case COL_WIDTH_HEAD:
                if (headCells) {
                    // 多行 head 仅使用最后一行校准
                    sheetColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                    writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 265);
                }
                break;
            // 使用内容列宽
            case COL_WIDTH_CONTENT:
                if (columnCells) {
                    sheetColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                    writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
                }
                break;
            // 默认列宽
            default:
                if (headCells) {
                    // 多行 head 仅使用最后一行校准
                    sheetColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                    writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 265);
                } else if (columnCells) {
                    sheetColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                    writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
                }
        }
    }

    /** 计算单元格数据宽度 */
    private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
        // 列名直接返回
        if (Boolean.TRUE.equals(isHead)) {
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
            case DATE:
                return "yyyy-MM-dd".getBytes().length;
            default:
                return -1;
        }
    }
}
