package com.demo;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 普通测试
 *
 * @author Song gh on 2022/5/6.
 */
class GeneralTest {

    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJC4BMkDUM5e2A5H96zbA4sfKMofzjb7/3IitFMGGVOXXYUi15YieiTUCphgL7tPYJVbwenwAIgrzEchCS7lyUdjwcJ06x0JIcsJIDLe3fMol+LSTH8vx78TrwfXvRm8dQtOnmXAYaULT17HK6lUFEGEmdib+vpIt7zADOaDXYJ9AgMBAAECgYAA7Wz6bM8Dw4/W55cqwGyRY627PeDwcUT90kMdlRhsdLfgtoxzJd1qhwFaYKNtq+COlHv1p9gZB07T1d5dMpPLotuO283SLCoPxybT3SomlW6z2iUrz0ykZL89kizV85PmwuiDqTylKippEIgqgQwqFH0T/SnJM2jwJ3YpUPz08QJBAMsGnfvp/WTXWlp1A3lVwRjKir0jtZ1Mzw577GeBVv3F3Y5gSMdisPxywgvC1loznEATIs+a70UIxoVDNd6wu4sCQQC2erS1iuPq2lKvhMaQMJPW8SHthq53Yr5pYnTI+drfsDDqIbphpbfUm0C6qM1cNoK8gYr8DiUawrt4xFbzvxsXAkBPPzT5eLsk2n51IomJmfR2ZdDDxSWF0c5ce/ip6i13fv1dLq4ZzacB0xV1G8cpjE2oIRAMcxCEJMnAiJyFYPzDAkAaFjapUV6931I8x1V/nYI1EynPhBaC+LnR5QJfDOEOY2jKv+GePgumuD8rsCATk7Ni8X4GBJunVLlqTV9E30gnAkBMdL0DNRnyPRmdEba0W5wP05cfnqf4ajxf2xFObpwu88+F2g41Ax3xgdYj/2ZXrAqo9vg2kJM/oKiBlFh/CqAK";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQuATJA1DOXtgOR/es2wOLHyjKH842+/9yIrRTBhlTl12FIteWInok1AqYYC+7T2CVW8Hp8ACIK8xHIQku5clHY8HCdOsdCSHLCSAy3t3zKJfi0kx/L8e/E68H170ZvHULTp5lwGGlC09exyupVBRBhJnYm/r6SLe8wAzmg12CfQIDAQAB";

    @Test
    void main() {
//        List<String> titleList = new ArrayList<>();
//        titleList.add("[\"ceshi1\"]");
//        titleList.add("[\"ceshi2\"]");
//        titleList.add("[\"ceshi3\"]");
//        String out = StringUtils.join(titleList, ',').replace("\"],[\"", "\",\"");
//        double test = (double) 6 / (double) 5;
//        System.out.println(BigDecimal.valueOf(test).setScale(0, RoundingMode.UP).intValue());
        System.out.println("00444".substring(1));
    }

    @Test
    void removeTrailing0() {
        try {
            System.out.println(new SimpleDateFormat("yyyy-MM-dd").parse("2022-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void colorTest() throws IOException {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("colorful");
        int rowNum = 0;
        for (IndexedColors color : IndexedColors.values()) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(color.toString());
            Cell cellColor = row.createCell(1);
            CellStyle style = wb.createCellStyle();
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(color.getIndex());
            cellColor.setCellStyle(style);
        }
        sheet.setColumnWidth(0, 25 * 256);
        sheet.setColumnWidth(1, 50 * 256);
        FileOutputStream fout = new FileOutputStream("/Users/collin/Downloads/color.xls");
        wb.write(fout);
        wb.close();
        fout.close();
    }

    /** 获取本机真实 ip */
    private InetAddress getRealIp() {
        try {
            Enumeration<NetworkInterface> networkEnum = NetworkInterface.getNetworkInterfaces();
            while (networkEnum.hasMoreElements()) {
                NetworkInterface currNetwork = networkEnum.nextElement();
                Enumeration<InetAddress> inetEnum = currNetwork.getInetAddresses();
                while (inetEnum.hasMoreElements()) {
                    InetAddress currInet = inetEnum.nextElement();
                    if (!currInet.isLoopbackAddress() && currInet.isSiteLocalAddress()) {
                        return currInet;
                    }
                }
            }
            return InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}