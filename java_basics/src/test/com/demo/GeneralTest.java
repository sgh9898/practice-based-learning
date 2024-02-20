package com.demo;

import com.demo.util.AesUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

/**
 * 普通测试
 *
 * @author Song gh on 2022/5/6.
 */
class GeneralTest {

    @Test
    void writeFile() {
        System.out.println(AesUtils.aesDecryptByKeyStrAndIvStr("W/iLblx71VN3TgJwSPK5KHUr/1h8er8N5+x83u5r6S8=", "PcWexLHEms67z5Rm", "y6vEtseyzIvMpMSv"));
    }

    @Test
    void test1() throws IOException {
        String str = "9a@_bc123";
        String number = str.replaceAll(".*\\D", "");
        System.out.println(number);
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