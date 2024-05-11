package com.sgh.demo.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片验证码工具
 *
 * @author Song gh
 * @version 2024/3/13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaptchaUtils {

// ------------------------------ 可变参数 ------------------------------
    /** 验证码位数 */
    private static final int CODE_LENGTH = 4;
    /** 验证码图片宽度 */
    private static final int IMAGE_WIDTH = 85;
    /** 验证码图片高度 */
    private static final int IMAGE_HEIGHT = 36;
    /** 随机颜色上限 */
    private static final int COLOR_MAX = 255;

// ------------------------------ 固定参数 ------------------------------
    /** 随机颜色下限 */
    private static final int COLOR_MIN = 1;
    /** 可选字体 */
    private static final String[] fontTypes = {"Arial", "Arial Black", "AvantGarde Bk BT", "Calibri", "Times New Roman",
            "宋体", "黑体", "Arial Unicode MS", "Lucida Sans"};
    /** 随机字符的范围 */
    private static final char[] codeSeq = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    /** 随机数生成器 */
    private static final SecureRandom random = new SecureRandom();

    /**
     * 生成验证码图片
     *
     * @return {"code": 验证码, "codeimg": 图片地址}
     */
    public static Map<String, String> createCodeImage() throws IOException {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        Graphics2D graphics2D = (Graphics2D) graphics;
        createBackground(graphics2D);

        // 生成字符
        String codeStr = createCharacter(graphics2D);
        graphics2D.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", outputStream);
        String base64 = Base64Utils.encodeToString(outputStream.toByteArray());

        // 返回数据
        Map<String, String> returnMap = new HashMap<>(16);
        returnMap.put("code", codeStr);
        returnMap.put("codeimg", "data:image/jpeg;base64," + base64);
        return returnMap;
    }
// ============================== 固定参数 Ends ==============================

// ------------------------------ Private 方法 ------------------------------

    /**
     * 生成随机颜色
     *
     * @param inputColorMin 产生颜色值下限
     * @param inputColorMax 产生颜色值上限
     * @return 生成的随机颜色对象
     */
    private static Color getRandomColor(int inputColorMin, int inputColorMax) {
        // 校验
        int colorMin = inputColorMin;
        int colorMax = inputColorMax;
        if (colorMin > COLOR_MAX) {
            colorMin = COLOR_MAX;
        }
        if (colorMin < COLOR_MIN) {
            colorMin = COLOR_MIN;
        }
        if (colorMax > COLOR_MAX) {
            colorMax = COLOR_MAX;
        }
        if (colorMax < COLOR_MIN) {
            colorMax = COLOR_MIN;
        }

        // 配置颜色
        int red = colorMin + random.nextInt(colorMax - colorMin);
        int blue = colorMin + random.nextInt(colorMax - colorMin);
        int green = colorMin + random.nextInt(colorMax - colorMin);
        return new Color(red, green, blue);
    }

    /** 生成验证码背景图 */
    private static void createBackground(Graphics2D graphics2D) {
        // 填充背景
        graphics2D.setColor(getRandomColor(220, 250));
        graphics2D.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        // 加入干扰线条
        int max = 7;
        for (int i = 0; i < max; i++) {
            graphics2D.setColor(getRandomColor(40, 150));
            int x = random.nextInt(IMAGE_WIDTH);
            int y = random.nextInt(IMAGE_HEIGHT);
            int x1 = random.nextInt(IMAGE_WIDTH);
            int y1 = random.nextInt(IMAGE_HEIGHT);
            graphics2D.drawLine(x, y, x1, y1);
        }
    }

    /** 生成随机字符 */
    private static String createCharacter(Graphics2D graphics2D) {
        StringBuilder codeStr = new StringBuilder();
        // 生成字符的字体大小
        int fontsize;
        // 生成字符 x 的位置
        int charX = 0;
        // 生成字符 y 的位置
        int chartY = IMAGE_HEIGHT - 10;
        Color color = graphics2D.getColor();
        // 旋转或错切
        int rotateOrShear = random.nextInt(2);
        // 逐个字符生成
        for (int i = 0; i < CODE_LENGTH; i++) {
            String codeChar = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);
            // 随机设置字体颜色
            graphics2D.setColor(new Color(50 + random.nextInt(100), 50 + random.nextInt(100), 50 + random.nextInt(100)));
            // 随机字体大小 18-22
            fontsize = Math.abs(18 + random.nextInt(4));
            Font font = new Font(fontTypes[random.nextInt(fontTypes.length)], Font.BOLD, fontsize);
            graphics2D.setFont(font);
            FontMetrics fontMetrics = new JLabel().getFontMetrics(font);
            // 当前随机生成字符的宽度
            int charWidth = fontMetrics.stringWidth("M");
            int charsRealWidth = charWidth * CODE_LENGTH;
            // 第一次循环的时候初始化
            if (i == 0 && (IMAGE_WIDTH > charsRealWidth)) {
                charX = (IMAGE_WIDTH - charsRealWidth) / 2;
            }
            if (rotateOrShear == 0) {
                // 画旋转文字
                double radianPercent = Math.PI * (random.nextInt(40) / 180D);
                if (random.nextBoolean()) {
                    radianPercent = -radianPercent;
                }
                graphics2D.rotate(radianPercent, charX + 9.0, chartY);
                graphics2D.drawString(codeChar, charX, chartY);
                graphics2D.rotate(-radianPercent, charX + 9.0, chartY);
            } else {
                graphics2D.drawString(codeChar, charX, chartY);
            }
            charX += charWidth;
            codeStr.append(codeChar);
        }
        // 错切
        if (rotateOrShear == 1) {
            shear(graphics2D, color);
        }
        return codeStr.toString();
    }

    /** 验证码图片错切 */
    private static void shear(Graphics graphics, Color color) {
        shearX(graphics, color);
        shearY(graphics, color);
    }

    /** 验证码图片错切 x 轴 */
    private static void shearX(Graphics graphics, Color color) {
        int period = random.nextInt(2);
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < IMAGE_HEIGHT; i++) {
            double d = (0) * Math.sin((double) i / (double) period + (6.2831853071795862D * phase) / frames);
            graphics.copyArea(0, i, IMAGE_WIDTH, 1, (int) d, 0);
            graphics.setColor(color);
            graphics.drawLine((int) d, i, 0, i);
            graphics.drawLine((int) d + IMAGE_WIDTH, i, IMAGE_WIDTH, i);
        }
    }

    /** 验证码图片错切 y 轴 */
    private static void shearY(Graphics g, Color color) {
        int period = random.nextInt(8) + 8;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < IMAGE_WIDTH; i++) {
            double d = (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * phase) / frames);
            g.copyArea(i, 0, 1, IMAGE_HEIGHT, 0, (int) d);
            g.setColor(color);
            g.drawLine(i, (int) d, i, 0);
            g.drawLine(i, (int) d + IMAGE_HEIGHT, i, IMAGE_HEIGHT);
        }
    }
}