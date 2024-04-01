package com.collin.demo.common.excel.easyexcel.handler;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.AbstractVerticalCellStyleStrategy;
import lombok.Getter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Excel 不指定对象时的样式, 与工具类 EasyExcelUtil 配合使用
 *
 * @author Song gh
 * @version 2024/1/30
 */
@Getter
public class ExcelNoModelVerticalStyleHandler extends AbstractVerticalCellStyleStrategy {

    /** 需要特殊处理的列名 */
    Set<String> specialHeadSet;

    public ExcelNoModelVerticalStyleHandler(@Nullable Set<String> specialHeadSet) {
        this.specialHeadSet = specialHeadSet;
        if (this.specialHeadSet == null) {
            this.specialHeadSet = new HashSet<>();
        }
    }

    /** head 样式 */
    @Override
    protected WriteCellStyle headCellStyle(Head head) {
        WriteCellStyle headStyle = new WriteCellStyle();
        // 位置
        headStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 颜色, 特殊列名标红
        headStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.index);
        for (String currHeadName : head.getHeadNameList()) {
            if (specialHeadSet.contains(currHeadName)) {
                headStyle.setFillForegroundColor(IndexedColors.TAN.index);
                break;
            }
        }
        // 字体
        WriteFont font = new WriteFont();
        font.setFontHeightInPoints((short) 13);
        headStyle.setWriteFont(font);

        return headStyle;
    }

    /** 内容样式 */
    @Override
    protected WriteCellStyle contentCellStyle(Head head) {
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        // 位置
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 字体
        WriteFont font = new WriteFont();
        font.setFontHeightInPoints((short) 12);
        writeCellStyle.setWriteFont(font);

        return writeCellStyle;
    }
}
