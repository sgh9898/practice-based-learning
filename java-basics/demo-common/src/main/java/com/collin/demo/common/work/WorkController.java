package com.collin.demo.common.work;

import com.collin.demo.common.excel.easyexcel.EasyExcelUtils;
import com.collin.demo.common.util.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 工作用接口
 *
 * @author Song gh
 * @version 2024/3/18
 */
@Slf4j
@Api(tags = "工作用接口")
@RestController
@RequestMapping("/work")
public class WorkController {

    @ApiOperation("导入数据中台主题域模型")
    @PostMapping("/dataCenter/model")
    public String dataCenterModel(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        List<DataCenterModelExcel> excelList = EasyExcelUtils.importExcel(file, request, response, DataCenterModelExcel.class);
        return JsonUtils.beanToJson(excelList);
    }

    @GetMapping("/dataCenter/exportTemplate")
    @ApiOperation("导出数据中台主题域模型模板")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) {
        EasyExcelUtils.exportTemplate(request, response, "主题域模型", DataCenterModelExcel.class, "");
    }

}
