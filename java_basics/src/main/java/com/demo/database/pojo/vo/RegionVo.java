package com.demo.database.pojo.vo;

import com.demo.database.db.entity.Region;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * [展示 VO] 区号
 *
 * @author Song gh on 2023/12/15.
 */
@Data
@ApiModel("[展示 VO] 区号")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegionVo {

    /** id */
    @ApiModelProperty("id")
    @JsonAlias("id")
    private Integer id;

    /** 长途区号 */
    @ApiModelProperty("长途区号")
    @JsonAlias("area_code")
    private String areaCode;

    /** 区号对应的省/直辖市/自治区行政区域编码 */
    @ApiModelProperty("区号对应的省/直辖市/自治区行政区域编码")
    @JsonAlias("province_code")
    private Integer provinceCode;

    /** 区号对应的省/直辖市/自治区全称 */
    @ApiModelProperty("区号对应的省/直辖市/自治区全称")
    @JsonAlias("province_name")
    private String provinceName;

    /** 区号对应的市级行政区域编码(多个用,隔开) */
    @ApiModelProperty("区号对应的市级行政区域编码(多个用,隔开)")
    @JsonAlias("city_codes")
    private String cityCodes;

    /** 区号对应的行政区域编码(多个用,隔开) */
    @ApiModelProperty("区号对应的行政区域编码(多个用,隔开)")
    @JsonAlias("district_codes")
    private String districtCodes;

    /** 区号对应的行政区域全称(多个用,隔开) */
    @ApiModelProperty("区号对应的行政区域全称(多个用,隔开)")
    @JsonAlias("district_name")
    private String districtName;

    /** 区号对应的行政区域全称和描述 */
    @ApiModelProperty("区号对应的行政区域全称和描述")
    @JsonAlias("district_name_des")
    private String districtNameDes;

    /** [构造] */
    public RegionVo() {
    }

    /** [构造] 根据实体类创建 */
    public RegionVo(Region entity) {
        this.id = entity.getId();
        this.areaCode = entity.getAreaCode();
        this.provinceCode = entity.getProvinceCode();
        this.provinceName = entity.getProvinceName();
        this.cityCodes = entity.getCityCodes();
        this.districtCodes = entity.getDistrictCodes();
        this.districtName = entity.getDistrictName();
        this.districtNameDes = entity.getDistrictNameDes();
    }
}

