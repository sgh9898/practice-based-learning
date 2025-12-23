package com.sgh.demo.common.util;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.awt.*;
import java.io.*;

/**
 * PDF 工具类
 *
 * @author Song gh
 * @since 2025/12/22
 */
public class PdfUtils {

    /** 默认字体名 */
    private static final String DEFAULT_FONT_NAME = "STSongStd-Light";
    /** 默认字体编码 */
    public static final String DEFAULT_FONT_ENCODING = "UniGB-UCS2-H";
    /** 默认字体大小 */
    private static final double DEFAULT_FONT_SIZE = 11.0;

    /** 水印字体名 */
    private static final String WATERMARK_FONT_NAME = "STSongStd-Light";
    /** 水印字体大小 */
    private static final float WATERMARK_FONT_SIZE = 50.0F;
    /** 水印字体颜色 */
    private static final BaseColor WATERMARK_FONT_COLOR = new BaseColor(240, 240, 240);
    /** 水印文字旋转角度 */
    private static final int WATERMARK_ROTATION = 30;
    /** 水印文字列数 */
    private static final int WATERMARK_COLUMNS = 1;
    /** 水印文字行数 */
    private static final int WATERMARK_ROWS = 1;


    /**
     * 将 docx 文件转换为 pdf 文件
     *
     * @param docxFilePath docx 文件路径
     * @param pdfFilePath  pdf 文件输出路径
     */
    public static void convertDocxToPdf(String docxFilePath, String pdfFilePath) {
        convertDocxToPdf(docxFilePath, pdfFilePath, null);
    }

    /**
     * 将 docx 文件转换为带水印的 pdf 文件
     *
     * @param docxFilePath  docx 文件路径
     * @param pdfFilePath   pdf 文件输出路径
     * @param watermarkText 水印文字(null 则不添加水印)
     */
    public static void convertDocxToPdf(String docxFilePath, String pdfFilePath, String watermarkText) {
        try (FileInputStream fis = new FileInputStream(docxFilePath);
             FileOutputStream fos = new FileOutputStream(pdfFilePath)) {
            convertDocxToPdf(fis, fos, watermarkText);
        } catch (IOException e) {
            throw new UnsupportedOperationException("转换 docx 文档至 pdf 失败", e);
        }
    }

    /**
     * 将 docx 输入流转为 pdf 输出流
     *
     * @param docxInputStream docx 输入流
     * @param pdfOutputStream pdf 输出流
     */
    public static void convertDocxToPdf(InputStream docxInputStream, OutputStream pdfOutputStream) {
        convertDocxToPdf(docxInputStream, pdfOutputStream, null);
    }

    /**
     * 将 docx 输入流转为带水印的 pdf 输出流
     *
     * @param docxInputStream docx 输入流
     * @param pdfOutputStream pdf 输出流
     * @param watermarkText   水印文字(null 则不添加水印)
     */
    public static void convertDocxToPdf(InputStream docxInputStream, OutputStream pdfOutputStream, String watermarkText) {
        try (XWPFDocument document = new XWPFDocument(docxInputStream)) {
            Document pdfDoc = new Document();
            PdfWriter writer = PdfWriter.getInstance(pdfDoc, pdfOutputStream);

            // 如果需要添加水印, 则设置事件处理器
            if (StringUtils.isNoneBlank(watermarkText)) {
                WatermarkEventHandler watermarkEventHandler = new WatermarkEventHandler(watermarkText);
                writer.setPageEvent(watermarkEventHandler);
            }

            pdfDoc.open();

            // 转换内容
            convertDocumentContent(document, pdfDoc);
            pdfDoc.close();
        } catch (IOException | DocumentException e) {
            throw new UnsupportedOperationException("转换 docx 文档至 pdf 失败", e);
        }
    }

// ------------------------------ 内部方法 ------------------------------

    /**
     * 转换 docx 文档内容至 pdf
     *
     * @param docxDocument docx
     * @param pdfDocument  pdf
     */
    private static void convertDocumentContent(XWPFDocument docxDocument, Document pdfDocument) {
        for (XWPFParagraph paragraph : docxDocument.getParagraphs()) {
            // 创建段落
            Paragraph pdfParagraph = new Paragraph();

            // 标记段落是否有内容
            boolean hasContent = false;

            // 设置段落对齐方式
            switch (paragraph.getAlignment()) {
                case CENTER:
                    pdfParagraph.setAlignment(Element.ALIGN_CENTER);
                    break;
                case RIGHT:
                    pdfParagraph.setAlignment(Element.ALIGN_RIGHT);
                    break;
                case BOTH:
                    pdfParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
                    break;
                default:
                    pdfParagraph.setAlignment(Element.ALIGN_LEFT);
            }

            // 处理段落中的文本
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null && !text.trim().isEmpty()) {
                    // 创建 chunk 并设置样式
                    Chunk chunk = createChunk(run, text);
                    // 添加到段落
                    pdfParagraph.add(chunk);
                    hasContent = true;
                }
            }

            try {
                // 只有当段落有实际内容时才添加到文档中
                if (hasContent) {
                    pdfDocument.add(pdfParagraph);
                }
            } catch (DocumentException e) {
                throw new UnsupportedOperationException("转换段落内容至 pdf 时出错", e);
            }
        }
    }

    /**
     * 创建 chunk 并设置样式
     *
     * @param run  段落中的运行对象
     * @param text 运行对象中的文本
     * @return 配置好样式的 chunk
     */
    private static Chunk createChunk(XWPFRun run, String text) {
        Chunk chunk = new Chunk(text);

        // 获取字体大小, 如果为 null 则默认 12
        Double fontSize = run.getFontSizeAsDouble();
        if (fontSize == null) {
            fontSize = DEFAULT_FONT_SIZE;
        }

        // 使用 iText Asian 支持库中的字体, 支持中英文
        Font font = FontFactory.getFont(DEFAULT_FONT_NAME, DEFAULT_FONT_ENCODING, BaseFont.EMBEDDED, fontSize.floatValue(), Font.NORMAL);

        // 设置字体样式
        if (run.isBold() && run.isItalic()) {
            font.setStyle(Font.BOLDITALIC);
        } else if (run.isBold()) {
            font.setStyle(Font.BOLD);
        } else if (font.isItalic()) {
            font.setStyle(Font.ITALIC);
        } else if (font.isUnderlined()) {
            font.setStyle(Font.UNDERLINE);
        } else if (font.isStrikethru()) {
            font.setStyle(Font.STRIKETHRU);
        } else {
            font.setStyle(Font.NORMAL);
        }

        // 设置颜色
        if (StringUtils.isNotBlank(run.getColor())) {
            Color color = Color.decode("#" + run.getColor());
            font.setColor(new BaseColor(color.getRGB()));
        }

        // 应用字体设置
        chunk.setFont(font);

        return chunk;
    }

    /** 水印事件处理器 */
    private static class WatermarkEventHandler extends PdfPageEventHelper {

        /** 水印文字 */
        private final String watermarkText;

        public WatermarkEventHandler(String watermarkText) {
            this.watermarkText = watermarkText;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            Font font = FontFactory.getFont(WATERMARK_FONT_NAME, DEFAULT_FONT_ENCODING, WATERMARK_FONT_SIZE, Font.NORMAL, WATERMARK_FONT_COLOR);

            // 获取页面尺寸
            float pageWidth = document.getPageSize().getWidth();
            float pageHeight = document.getPageSize().getHeight();

            // 设置水印的行列数和间距
            int rows = WATERMARK_ROWS;
            int cols = WATERMARK_COLUMNS;
            float rowSpacing = pageHeight / (rows + 1);
            float colSpacing = pageWidth / (cols + 1);

            // 绘制多行水印
            for (int row = 1; row <= rows; row++) {
                for (int col = 1; col <= cols; col++) {
                    float x = col * colSpacing;
                    float y = row * rowSpacing;

                    // 创建列对象
                    ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(watermarkText, font),
                            x, y, WATERMARK_ROTATION);
                }
            }
        }
    }
}