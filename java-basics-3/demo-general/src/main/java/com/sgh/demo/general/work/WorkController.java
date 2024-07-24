package com.sgh.demo.common.work;

import com.sgh.demo.common.util.JsonUtils;
import com.sgh.demo.general.excel.easyexcel.EasyExcelUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 工作用接口
 *
 * @author Song gh
 * @version 2024/3/18
 */
@Slf4j
@Tag(name = "工作用接口")
@RestController
@RequestMapping("/work")
public class WorkController {

    @Operation(summary = "导入数据中台主题域模型")
    @PostMapping("/dataCenter/model")
    public String dataCenterModel(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        List<com.sgh.demo.common.work.DataCenterModelExcel> excelList = EasyExcelUtils.importExcel(file, request, response, com.sgh.demo.common.work.DataCenterModelExcel.class);
        return JsonUtils.beanToJson(excelList);
    }

    @GetMapping("/dataCenter/exportTemplate")
    @Operation(summary = "导出数据中台主题域模型模板")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) {
        EasyExcelUtils.exportTemplate(request, response, "主题域模型", com.sgh.demo.common.work.DataCenterModelExcel.class, "");
    }

}
