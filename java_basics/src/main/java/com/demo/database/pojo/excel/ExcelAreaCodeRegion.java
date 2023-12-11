package com.demo.database.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.demo.database.db.entity.AreaCodeRegion;
import com.demo.excel.easyexcel.EasyExcelClassTemplate;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

/**
 * [Excel 导入/导出] 区号
 *
 * @author Song gh on 2023/12/11.
 */
@Getter
@Setter
@ApiModel("[Excel 导入/导出] 区号")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExcelAreaCodeRegion extends EasyExcelClassTemplate {

    @NotBlank(message = "长途区号未填写")
    @ExcelProperty("长途区号")
    @JsonAlias("area_code")
    private String areaCode;

    @NotNull(message = "区号对应的省/直辖市/自治区行政区域编码未填写")
    @PositiveOrZero(message = "区号对应的省/直辖市/自治区行政区域编码超出范围")
    @ExcelProperty("区号对应的省/直辖市/自治区行政区域编码")
    @JsonAlias("province_code")
    private Integer provinceCode;

    @NotBlank(message = "区号对应的省/直辖市/自治区全称未填写")
    @ExcelProperty("区号对应的省/直辖市/自治区全称")
    @JsonAlias("province_name")
    private String provinceName;

    @NotBlank(message = "区号对应的市级行政区域编码(多个用,隔开)未填写")
    @ExcelProperty("区号对应的市级行政区域编码(多个用,隔开)")
    @JsonAlias("city_codes")
    private String cityCodes;

    @NotBlank(message = "区号对应的行政区域编码(多个用,隔开)未填写")
    @ExcelProperty("区号对应的行政区域编码(多个用,隔开)")
    @JsonAlias("district_codes")
    private String districtCodes;

    @NotBlank(message = "区号对应的行政区域全称(多个用,隔开)未填写")
    @ExcelProperty("区号对应的行政区域全称(多个用,隔开)")
    @JsonAlias("district_name")
    private String districtName;

    @NotBlank(message = "区号对应的行政区域全称和描述未填写")
    @ExcelProperty("区号对应的行政区域全称和描述")
    @JsonAlias("district_name_des")
    private String districtNameDes;
    
    /** [构造] */
    public ExcelAreaCodeRegion() {
    }
    
    /**
     * [构造] 根据实体类创建
     * p.s. 在 jpa 中直接使用 hql 语法创建时, 需要对入参实体类进行 null 判断, 否则会产生报错
     */
    public ExcelAreaCodeRegion(AreaCodeRegion input) {
        if (input != null) {
            this.areaCode = input.getAreaCode();
            this.provinceCode = input.getProvinceCode();
            this.provinceName = input.getProvinceName();
            this.cityCodes = input.getCityCodes();
            this.districtCodes = input.getDistrictCodes();
            this.districtName = input.getDistrictName();
            this.districtNameDes = input.getDistrictNameDes();
        }
    }
}
