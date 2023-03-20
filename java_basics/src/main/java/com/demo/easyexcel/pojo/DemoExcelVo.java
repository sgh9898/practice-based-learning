package com.demo.easyexcel.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.demo.database.entity.DemoEntity;
import com.demo.easyexcel.util.annotation.ExcelDropDown;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateEntity;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateExcelVo;
import com.demo.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 演示类, 用于 Excel[导入/导出]
 * <br> 非必填项需设置默认值, 防止 null 报错
 *
 * @author Song gh on 2023/02/28.
 */
@Getter
@Setter
@ApiModel(description = "演示类 Excel")
public class DemoExcelVo extends EasyExcelTemplateExcelVo {

    @NotBlank(message = "名称未填写")
    @ExcelProperty("名称")
    private String name;

    @NotBlank(message = "标签未填写")
    @ExcelProperty("标签")
    private String tags;

    @NotNull(message = "日期未填写")
    @ExcelProperty("日期")
    @DateTimeFormat("yyyy/MM/dd")
    private Date dateTime;

    /**
     * [使用默认配置时无须 override] 在默认的 BeanUtils.copyProperties 之后, 手动定义 Excel 中部分参数
     *
     * @param entity 需要转换的 Entity 类, 必须 extends {@link EasyExcelTemplateEntity}
     */
    @Override
    public <T extends EasyExcelTemplateEntity> void setParamsAfterCopy(T entity) {
    }

}
