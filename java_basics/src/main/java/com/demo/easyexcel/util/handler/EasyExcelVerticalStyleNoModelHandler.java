package com.demo.easyexcel.util.handler;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.AbstractVerticalCellStyleStrategy;
import lombok.Getter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Excel 不指定对象时的样式, 与工具类 EasyExcelUtil 配合使用
 *
 * @author Song gh on 2022/9/27.
 */
@Getter
public class EasyExcelVerticalStyleNoModelHandler extends AbstractVerticalCellStyleStrategy {
    /**
     * Returns the column width corresponding to each column head
     *
     * @param head Nullable
     */
    @Override
    protected WriteCellStyle headCellStyle(Head head) {
        WriteCellStyle headStyle = new WriteCellStyle();
        // 位置
        headStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 颜色
        headStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.index);
        // 字体
        WriteFont font = new WriteFont();
        font.setFontHeightInPoints((short) 13);
        headStyle.setWriteFont(font);
        return headStyle;
    }

    /** 内容 */
    @Override
    protected WriteCellStyle contentCellStyle(Head head) {
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        return writeCellStyle;
    }
}
