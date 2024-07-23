package com.sgh.demo.general.excel.service;

import com.sgh.demo.common.util.CnStrUtils;
import com.sgh.demo.general.database.db.repository.DemoEntityRepository;
import com.sgh.demo.general.database.pojo.excel.ExcelDemoEntity;
import com.sgh.demo.general.excel.easyexcel.EasyExcelUtils;
import com.sgh.demo.general.excel.easyexcel.pojo.EasyExcelExportDto;
import com.sgh.demo.general.excel.easyexcel.pojo.EasyExcelNoModelExportDto;
import com.sgh.demo.general.excel.easyexcel.pojo.ExcelCascadeOption;
import com.sgh.demo.general.excel.pojo.EasyExcelTest;
import com.sgh.demo.general.excel.pojo.ExcelToDdl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.IntStream;

/**
 * EasyExcel 功能测试
 *
 * @author Song gh on 2023/3/27.
 */
@Service
public class ExcelService {

    @Resource
    private DemoEntityRepository demoEntityRepository;

    /** 导入数据 */
    public Object importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        return EasyExcelUtils.importExcel(file, request, response, ExcelToDdl.class);
    }

    /** 导出模板 */
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) {
        EasyExcelUtils.exportTemplate(request, response, "模板样例", ExcelToDdl.class, "这是一条说明");
    }

    /** 导出数据 */
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        List<ExcelDemoEntity> excelList = demoEntityRepository.getExcelList();
        EasyExcelUtils.exportData(request, response, "数据样例", ExcelDemoEntity.class, excelList);
    }

    /** 导出自定义数据 */
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        EasyExcelExportDto exportDto = new EasyExcelExportDto();
        exportDto.setTitle("这是标题");
        exportDto.setNote("这是说明");
        exportDto.getExcludedCols().add("comment");
        exportDto.getReplaceHeadMap().put("类型", "新类型");
        exportDto.getDynamicMenuMap().put("动态head",
                new String[]{"类型1", "类型2"});

        List<ExcelCascadeOption> nameCascadeList = getExcelCascadeOptions();
        exportDto.setCascadeMenu(nameCascadeList);

        EasyExcelUtils.exportErrorExcel(request, response, EasyExcelTest.class, null, exportDto);
    }

    /** 无模板导出 */
    public void noModelExportExcel(HttpServletRequest request, HttpServletResponse response) {
        // 列名
        List<List<String>> outerHeadList = new LinkedList<>();
        List<String> headList1 = new ArrayList<>();
        headList1.add("testHead1");
        headList1.add("testHead2");
        List<String> headList2 = new ArrayList<>();
        headList2.add("testHead1");
        headList2.add("testHead3");
        List<String> headList3 = new ArrayList<>();
        headList3.add("testHead4");
        List<String> headList4 = new ArrayList<>();
        headList4.add("testHead5");
        outerHeadList.add(headList1);
        outerHeadList.add(headList2);
        outerHeadList.add(headList3);
        outerHeadList.add(headList4);

        // 标红
        Set<String> importantSet = new HashSet<>();
        importantSet.add("testHead3");

        Map<String, String> enToCnMap = new HashMap<>();
        enToCnMap.put("testHead1", "测试标题1");

        // 导出
        EasyExcelNoModelExportDto exportDto = new EasyExcelNoModelExportDto();
        exportDto.setFileName("测试文件noModel");
        exportDto.setEnToCnHeadMap(enToCnMap);
        exportDto.setEnHeadList(outerHeadList);
        exportDto.setSheetName("11111");
        exportDto.setImportantHeadSet(importantSet);
        List<ExcelCascadeOption> nameCascadeList = getExcelCascadeOptions();
        exportDto.getCascadeMenuMap().put("cascade", nameCascadeList);
        List<String> cascadeColList = new LinkedList<>();
        cascadeColList.add("testHead3");
        cascadeColList.add("testHead4");
        cascadeColList.add("testHead5");
        exportDto.getCascadeColMap().put("cascade", cascadeColList);
        EasyExcelUtils.noModelExportExcel(request, response, exportDto);
    }

    /** 生成建表语句 */
    public String generateDdl(MultipartFile file, HttpServletRequest request, HttpServletResponse response, String tableName, Boolean exportExcel) {
        List<ExcelToDdl> sqlList = EasyExcelUtils.importExcel(file, request, response, ExcelToDdl.class);
        StringBuilder ddl = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        ddl.append(tableName.toLowerCase().trim()).append(" (");
        assert sqlList != null;
        for (ExcelToDdl currLine : sqlList) {
            dealWithOneRow(currLine, ddl);
        }
        ddl.deleteCharAt(ddl.lastIndexOf(","));
        ddl.append(") COMMENT '' COLLATE = utf8mb4_general_ci;");

        if (exportExcel == Boolean.TRUE) {
            EasyExcelUtils.exportData(request, response, "数据格式", ExcelToDdl.class, sqlList);
            return null;
        }
        return ddl.toString();
    }

    /** 联动下拉框 */
    @NonNull
    private static List<ExcelCascadeOption> getExcelCascadeOptions() {
        List<ExcelCascadeOption> nameCascadeList = new ArrayList<>();
        ExcelCascadeOption excelCascadeOption = new ExcelCascadeOption("第一层1");

        List<ExcelCascadeOption> nameCascadeList2 = new ArrayList<>();
        ExcelCascadeOption excelCascadeOption2 = new ExcelCascadeOption("第二层11");
        List<ExcelCascadeOption> nameCascadeList3 = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            ExcelCascadeOption excelCascadeOption3 = new ExcelCascadeOption("第三层11" + i);
            nameCascadeList3.add(excelCascadeOption3);
        });
        excelCascadeOption2.setChildList(nameCascadeList3);
        nameCascadeList2.add(excelCascadeOption2);

        excelCascadeOption2 = new ExcelCascadeOption("第二层12");
        nameCascadeList2.add(excelCascadeOption2);

        excelCascadeOption.setChildList(nameCascadeList2);
        nameCascadeList.add(excelCascadeOption);

        excelCascadeOption = new ExcelCascadeOption("第一层2");

        nameCascadeList2 = new ArrayList<>();
        excelCascadeOption2 = new ExcelCascadeOption("第二层21");
        nameCascadeList2.add(excelCascadeOption2);

        excelCascadeOption2 = new ExcelCascadeOption("第二层22");
        nameCascadeList2.add(excelCascadeOption2);

        excelCascadeOption.setChildList(nameCascadeList2);
        nameCascadeList.add(excelCascadeOption);

        IntStream.range(2, 11).forEach(i -> {
            ExcelCascadeOption item = new ExcelCascadeOption("第一层" + i);
            nameCascadeList.add(item);
        });
        return nameCascadeList;
    }

// ------------------------------ Private ------------------------------

    /** 根据 excel 单行数据构建 ddl */
    private void dealWithOneRow(ExcelToDdl excelToDdl, StringBuilder ddl) {
        // 校验必填项
        if (StringUtils.isBlank(excelToDdl.getType())) {
            return;
        }

        if (StringUtils.isBlank(excelToDdl.getName()) && (StringUtils.isNotBlank(excelToDdl.getComment()))) {
            excelToDdl.setName(CnStrUtils.getFirstLettersLower(excelToDdl.getComment()));
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
        if (str == null || str.trim().isEmpty()) {
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
