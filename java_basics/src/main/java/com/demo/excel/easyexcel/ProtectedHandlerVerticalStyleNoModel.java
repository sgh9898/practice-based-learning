package com.demo.excel.easyexcel;

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
class ProtectedHandlerVerticalStyleNoModel extends AbstractVerticalCellStyleStrategy {

    /** head 样式 */
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
