package com.demo.database.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.demo.database.db.entity.Region;
import com.demo.excel.easyexcel.EasyExcelClassTemplate;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

/**
 * [Excel 导入/导出] 区号
 *
 * @author Song gh
 * @version 2024/02/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("[Excel 导入/导出] 区号")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExcelRegion extends EasyExcelClassTemplate {

    @NotBlank(message = "长途区号未填写")
    @JsonAlias("area_code")
    @ExcelProperty("长途区号")
    private String areaCode;

    @NotNull(message = "区号对应的省/直辖市/自治区行政区域编码未填写")
    @PositiveOrZero(message = "区号对应的省/直辖市/自治区行政区域编码超出范围")
    @JsonAlias("province_code")
    @ExcelProperty("区号对应的省/直辖市/自治区行政区域编码")
    private Integer provinceCode;

    @NotBlank(message = "区号对应的省/直辖市/自治区全称未填写")
    @JsonAlias("province_name")
    @ExcelProperty("区号对应的省/直辖市/自治区全称")
    private String provinceName;

    @NotBlank(message = "区号对应的市级行政区域编码(多个用,隔开)未填写")
    @JsonAlias("city_codes")
    @ExcelProperty("区号对应的市级行政区域编码(多个用,隔开)")
    private String cityCodes;

    @NotBlank(message = "区号对应的行政区域编码(多个用,隔开)未填写")
    @JsonAlias("district_codes")
    @ExcelProperty("区号对应的行政区域编码(多个用,隔开)")
    private String districtCodes;

    @NotBlank(message = "区号对应的行政区域全称(多个用,隔开)未填写")
    @JsonAlias("district_name")
    @ExcelProperty("区号对应的行政区域全称(多个用,隔开)")
    private String districtName;

    @NotBlank(message = "区号对应的行政区域全称和描述未填写")
    @JsonAlias("district_name_des")
    @ExcelProperty("区号对应的行政区域全称和描述")
    private String districtNameDes;

    @ExcelProperty("创建时间")
    @DateTimeFormat("yyyy-MM-dd")
    private Date createTime;

    /** [构造] */
    public ExcelRegion() {
    }

    /**
     * [构造] 根据实体类创建
     * p.s. 在 jpa 中直接使用 hql 语法创建时, 需要对入参实体类进行 null 判断, 否则会产生报错
     */
    public ExcelRegion(Region input) {
        if (input != null) {
            this.areaCode = input.getAreaCode();
            this.provinceCode = input.getProvinceCode();
            this.provinceName = input.getProvinceName();
            this.cityCodes = input.getCityCodes();
            this.districtCodes = input.getDistrictCodes();
            this.districtName = input.getDistrictName();
            this.districtNameDes = input.getDistrictNameDes();
            this.createTime = input.getCreateTime();
        }
    }
}
