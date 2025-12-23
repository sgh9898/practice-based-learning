package com.sgh.demo.general.temp;


import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sgh.demo.general.excel.easyexcel.BaseEasyExcelClass;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * [实体类] 学生档案信息表
 *
 * @author Song gh
 * @version 2025/4/1
 */
@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentFiles extends BaseEasyExcelClass {

    /** 学生档案信息uid */
    @Id
    @JsonAlias("uid")
    @ExcelProperty("学生档案信息uid")
    private String uid;

    /** 学生姓名 */
    @JsonAlias("student_name")
    @ExcelProperty("学生姓名")
    private String studentName;

    /** 证件号码 */
    @JsonAlias("certificate_number")
    @ExcelProperty("证件号码")
    private String certificateNumber;
}
