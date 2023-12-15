package com.demo.database.db.entity;

import com.demo.database.pojo.excel.ExcelRegion;
import com.demo.database.pojo.upsert.RegionUpsertDto;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * [实体类] 区号
 *
 * @author Song gh on 2023/12/15.
 */
@Entity
@Getter
@Setter
@Table(name = "region")
@ApiModel("[实体类] 区号")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Region {

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    /** 逻辑删除: 1-删除, 0-未删除 */
    @JsonIgnore
    @ApiModelProperty(value = "逻辑删除: 1-删除, 0-未删除", hidden = true)
    @JsonAlias("is_deleted")
    private Boolean isDeleted;

    /** 创建时间 */
    @JsonIgnore
    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonAlias("create_time")
    private Date createTime;

    /** 更新时间 */
    @JsonIgnore
    @ApiModelProperty(value = "更新时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonAlias("update_time")
    private Date updateTime;

    /** [构造] */
    public Region() {
        init();
    }

    /** [构造] 根据 dto 创建 */
    public Region(RegionUpsertDto dto) {
        init();
        update(dto);
    }

    /** [构造] 根据 excel 创建 */
    public Region(ExcelRegion excel) {
        init();
        this.areaCode = excel.getAreaCode();
        this.provinceCode = excel.getProvinceCode();
        this.provinceName = excel.getProvinceName();
        this.cityCodes = excel.getCityCodes();
        this.districtCodes = excel.getDistrictCodes();
        this.districtName = excel.getDistrictName();
        this.districtNameDes = excel.getDistrictNameDes();
    }

    /** 初始化 */
    public void init() {
        this.isDeleted = Boolean.FALSE;
        this.createTime = new Date();
        this.updateTime = this.createTime;
    }

    /** 根据 dto 更新 */
    public void update(RegionUpsertDto dto) {
        this.areaCode = dto.getAreaCode();
        this.provinceCode = dto.getProvinceCode();
        this.provinceName = dto.getProvinceName();
        this.cityCodes = dto.getCityCodes();
        this.districtCodes = dto.getDistrictCodes();
        this.districtName = dto.getDistrictName();
        this.districtNameDes = dto.getDistrictNameDes();
        // 默认配置
        this.updateTime = new Date();
    }
}
