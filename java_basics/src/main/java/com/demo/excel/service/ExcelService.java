package com.demo.excel.service;

import com.demo.database.entity.DemoEntity;
import com.demo.database.repository.DemoEntityRepository;
import com.demo.excel.easyexcel.EasyExcelUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EasyExcel 功能测试
 *
 * @author Song gh on 2023/3/27.
 */
@Service
public class ExcelService {

    @Resource
    private DemoEntityRepository demoEntityRepository;

    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        EasyExcelUtils excelUtils = new EasyExcelUtils(request, response, "Excel导出测试", DemoEntity.class);
        excelUtils.setTitle("这是一条标题");
        excelUtils.exportExcelCustomized();
    }
}
