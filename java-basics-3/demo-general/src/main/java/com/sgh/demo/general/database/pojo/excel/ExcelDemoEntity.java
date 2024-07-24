package com.sgh.demo.common.database.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sgh.demo.common.database.db.entity.DemoEntity;
import com.sgh.demo.general.excel.easyexcel.EasyExcelClassTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * [Excel 导入/导出] 测试数据
 *
 * @author Song gh
 * @version 2024/03/18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "[Excel 导入/导出] 测试数据")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExcelDemoEntity extends EasyExcelClassTemplate {

    @NotBlank(message = "名称未填写")
    @JsonAlias("name")
    @ExcelProperty("名称")
    private String name;

    @NotNull(message = "数字未填写")
    @PositiveOrZero(message = "数字超出范围")
    @JsonAlias("num")
    @ExcelProperty("数字")
    private Integer num;

    @NotNull(message = "时间未填写")
    @JsonAlias("date")
    @ExcelProperty("时间")
    @DateTimeFormat("yyyy-MM-dd")
    private Date date;

    @NotBlank(message = "备注未填写")
    @JsonAlias("comment")
    @ExcelProperty("备注")
    private String comment;

    /** [构造] */
    public ExcelDemoEntity() {
    }

    /**
     * [构造] 根据实体类创建
     * p.s. 在 jpa 中直接使用 hql 语法创建时, 需要对入参实体类进行 null 判断, 否则会产生报错
     */
    public ExcelDemoEntity(DemoEntity entity) {
        if (entity != null) {
            this.name = entity.getName();
            this.num = entity.getNum();
            this.date = entity.getDate();
            this.comment = entity.getComment();
        }
    }
}
