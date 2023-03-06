package com.demo.database.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.demo.database.entity.DemoEntity;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateExcelVo;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 演示类, 用于 Excel[导入/导出]
 * <br> 非必填项需设置默认值, 防止 null 报错
 *
 * @author Song gh on 2023/02/28.
 */
@Getter
@Setter
@ApiModel(description = "演示类 Excel")
public class ExcelDemoExcelVo extends EasyExcelTemplateExcelVo {

    @NotBlank(message = "名称未填写")
    @ExcelProperty("名称")
    private String name;

    @NotBlank(message = "标签未填写")
    @ExcelProperty("标签")
    private String tags;
    
    /** Constructor */
    public ExcelDemoExcelVo() {
    }
    
    /** Constructor */
    public ExcelDemoExcelVo(DemoEntity input) {
        if (input != null) {
            this.name = input.getName();
            this.tags = input.getTags();
        }
    }
}
