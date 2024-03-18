package com.demo;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 普通测试
 *
 * @author Song gh on 2022/5/6.
 */
class GeneralTest {

    /** 十六位密钥(需要前端和后端保持一致), 更换项目时必须重新生成 */
    private static final String DEFAULT_KEY_STR = "pcWGuS2nQF11Sf+y";
    /** 十六位密钥偏移量(需要前端和后端保持一致), 更换项目时必须重新生成 */
    private static final String DEFAULT_IV_STR = "kvJRbJz7x5ycy+4V";

    @Test
    void writeFile() {
        System.out.println(Boolean.TRUE.toString());
    }

    public static void main(String[] args) {
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