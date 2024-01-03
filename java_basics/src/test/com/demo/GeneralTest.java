package com.demo;

import okhttp3.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

/**
 * 普通测试
 *
 * @author Song gh on 2022/5/6.
 */
class GeneralTest {

    /** bucket 匿名读取文件配置(允许前端通过 url 访问) */
    private static final String READ_ONLY_RULES_TEMPLATE = "{\n" +
            "    \"Version\": \"2012-10-17\",\n" +
            "    \"Statement\": [\n" +
            "        {\n" +
            "            \"Effect\": \"Allow\",\n" +
            "            \"Principal\": {\n" +
            "                \"AWS\": [\n" +
            "                    \"*\"\n" +
            "                ]\n" +
            "            },\n" +
            "            \"Action\": [\n" +
            "                \"s3:GetObject\"\n" +
            "            ],\n" +
            "            \"Resource\": [\n" +
            "                \"arn:aws:s3:::%s/%s*\"\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJC4BMkDUM5e2A5H96zbA4sfKMofzjb7/3IitFMGGVOXXYUi15YieiTUCphgL7tPYJVbwenwAIgrzEchCS7lyUdjwcJ06x0JIcsJIDLe3fMol+LSTH8vx78TrwfXvRm8dQtOnmXAYaULT17HK6lUFEGEmdib+vpIt7zADOaDXYJ9AgMBAAECgYAA7Wz6bM8Dw4/W55cqwGyRY627PeDwcUT90kMdlRhsdLfgtoxzJd1qhwFaYKNtq+COlHv1p9gZB07T1d5dMpPLotuO283SLCoPxybT3SomlW6z2iUrz0ykZL89kizV85PmwuiDqTylKippEIgqgQwqFH0T/SnJM2jwJ3YpUPz08QJBAMsGnfvp/WTXWlp1A3lVwRjKir0jtZ1Mzw577GeBVv3F3Y5gSMdisPxywgvC1loznEATIs+a70UIxoVDNd6wu4sCQQC2erS1iuPq2lKvhMaQMJPW8SHthq53Yr5pYnTI+drfsDDqIbphpbfUm0C6qM1cNoK8gYr8DiUawrt4xFbzvxsXAkBPPzT5eLsk2n51IomJmfR2ZdDDxSWF0c5ce/ip6i13fv1dLq4ZzacB0xV1G8cpjE2oIRAMcxCEJMnAiJyFYPzDAkAaFjapUV6931I8x1V/nYI1EynPhBaC+LnR5QJfDOEOY2jKv+GePgumuD8rsCATk7Ni8X4GBJunVLlqTV9E30gnAkBMdL0DNRnyPRmdEba0W5wP05cfnqf4ajxf2xFObpwu88+F2g41Ax3xgdYj/2ZXrAqo9vg2kJM/oKiBlFh/CqAK";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQuATJA1DOXtgOR/es2wOLHyjKH842+/9yIrRTBhlTl12FIteWInok1AqYYC+7T2CVW8Hp8ACIK8xHIQku5clHY8HCdOsdCSHLCSAy3t3zKJfi0kx/L8e/E68H170ZvHULTp5lwGGlC09exyupVBRBhJnYm/r6SLe8wAzmg12CfQIDAQAB";

    @Test
    void test() throws IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, MalformedURLException {
        String strOut = String.format(READ_ONLY_RULES_TEMPLATE, "ceshi", "");
        System.out.println(strOut);
    }

    @Test
    void test1() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"orgCode\": \"LJTS\",\n    \"reportedNo\": \"IAL3fx8dGEHaaPzIPvE/Pg==\",\n    \"reportingNo\": \"1oJhxRyiT8crGlMQu8aJvQ==\"\n}");
        Request request = new Request.Builder()
                .url("https://caictdnc.caict.ac.cn:1064/dnc/v1/query")
                .method("POST", body)
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .addHeader("Host", "caictdnc.caict.ac.cn:1064")
                .addHeader("Connection", "keep-alive")
                .build();
        Response response = client.newCall(request).execute();
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