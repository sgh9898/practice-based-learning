package com.demo.excel.service;

import com.demo.database.pojo.excel.ExcelAreaCodeRegion;
import com.demo.excel.easyexcel.EasyExcelUtils;
import com.demo.excel.pojo.ExcelToDdl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * EasyExcel 功能测试
 *
 * @author Song gh on 2023/3/27.
 */
@Service
public class ExcelService {

    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        EasyExcelUtils excelUtils = new EasyExcelUtils(request, response, "Excel导出测试", ExcelAreaCodeRegion.class);
        excelUtils.setTitle("这是一条标题");
        excelUtils.exportExcelCustomized();
    }

    /** 无模板导出 */
    public void noModelExport(HttpServletRequest request, HttpServletResponse response) {
        // 标题
        List<List<String>> outerHeadList = new LinkedList<>();
        List<String> headList1 = new ArrayList<>();
        headList1.add("testHead1");
        headList1.add("testHead2");
        List<String> headList2 = new ArrayList<>();
        headList2.add("testHead1");
        headList2.add("testHead3");
        List<String> headList3 = new ArrayList<>();
        headList3.add("testHead4");
        outerHeadList.add(headList1);
        outerHeadList.add(headList2);
        outerHeadList.add(headList3);

        Set<String> importantSet = new HashSet<>();
        importantSet.add("testHead3");

        Map<String, String> enToCnMap = new HashMap<>();
        enToCnMap.put("testHead1", "测试标题1");
        EasyExcelUtils easyExcelUtils = new EasyExcelUtils(request, response, "测试文件noModel");
        easyExcelUtils.setNoModelEnToCnHeadNameMap(enToCnMap);
        easyExcelUtils.setNoModelEnHeadList(outerHeadList);
        easyExcelUtils.setSheetName("11111");
        easyExcelUtils.setNoModelEnSpecialHeadSet(importantSet);
        easyExcelUtils.noModelExportExcelCustomized();
    }

    /** 生成建表语句 */
    public String generateDdl(MultipartFile file, HttpServletRequest request, HttpServletResponse response, String tableName) {
        List<ExcelToDdl> sqlList = EasyExcelUtils.importData(file, request, response, ExcelToDdl.class, null);
        StringBuffer ddl = new StringBuffer("create table if not exists ");
        ddl.append(tableName.toLowerCase().trim()).append(" (");
        assert sqlList != null;
        for (ExcelToDdl currLine : sqlList) {
            dealWithOneRow(currLine, ddl);
        }
        ddl.deleteCharAt(ddl.lastIndexOf(","));
        ddl.append(") comment '';");

        return ddl.toString();
    }

// ------------------------------ Private ------------------------------

    /** 根据 excel 单行数据构建 ddl */
    private void dealWithOneRow(ExcelToDdl excelToDdl, StringBuffer ddl) {
        // 校验必填项 name, type
        if (StringUtils.isBlank(excelToDdl.getName()) || StringUtils.isBlank(excelToDdl.getType())) {
            return;
        }

        // name, 转为下划线格式
        String name = camelToSnake(excelToDdl.getName().trim());

        ddl.append(name).append(' ');

        // type
        String type = excelToDdl.getType()
                .toLowerCase().trim()
                .replace('（', '(')
                .replace('）', ')')      // brackets in Chinese
                .replace("long", "bigint")
                .replace("string", "varchar(128)");    // Java type to Database type
        ddl.append(type.equals("varchar") ? "varchar(128)" : type)
                .append(' ');       // 未定长度按默认值

        // required
        String required = excelToDdl.getRequired().trim();
        ddl.append(required.equals("是") || required.equals("必填") ? "not null" : "null")
                .append(' ');

        // comment
        String comment = excelToDdl.getComment().trim();
        ddl.append("comment '")
                .append(comment)
                .append("'");

        // primary key
        String primaryKey = excelToDdl.getPrimaryKey().trim();
        ddl.append(primaryKey.equals("是") ? " primary key," : ", ");
    }

    /** String 驼峰转下划线 */
    private static String camelToSnake(String str) {
        if (str == null || "".equals(str.trim())) {
            return "";
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                sb.append("_");
            }
            sb.append(Character.toLowerCase(ch));
        }
        return sb.toString();
    }
}
