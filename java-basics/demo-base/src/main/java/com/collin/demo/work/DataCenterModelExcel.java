package com.collin.demo.work;

import com.alibaba.excel.annotation.ExcelProperty;
import com.collin.demo.excel.easyexcel.EasyExcelClassTemplate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 数据中台导入模型
 *
 * @author Song gh
 * @version 2024/3/18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCenterModelExcel extends EasyExcelClassTemplate {

    @ExcelProperty("id")
    private long id;

    @ExcelProperty("modelId")
    private int modelId;

    @ExcelProperty("中文名")
    private String name;

    private String type;

    @ExcelProperty("字段名")
    private String columnName;

    @ExcelProperty("数据库类型")
    private String columnType;

    @ExcelProperty("说明")
    private String comment;

    private int primaryKey = 0;

    private int autoIncrement = 0;

    @ExcelProperty("非空")
    private Integer notNull = 0;

    private String defaultValue = "";

    private int secret = 0;

    private int searchTerm = 0;

    private Object followModelId;

    private Object followColumnId;

    private Object followType;

    public DataCenterModelExcel() {
    }

    public void setType(String type) {
    }

    public void setColumnName(String columnName) {
        this.columnName = camelToSnake(columnName);
    }

    public void setColumnType(String columnType) {
        this.columnType = StringUtils.upperCase(columnType);
        if ("VARCHAR".equals(this.columnType)) {
            this.columnType = "VARCHAR(128)";
        }
        if ("INT".equals(this.columnType)) {
            this.type = "整数";
        } else if ("BIGINT".equals(this.columnType)) {
            this.type = "大整数";
        } else if ("VARCHAR(128)".equals(this.columnType)) {
            this.type = "字串-短";
        }
    }

    public void setNotNull(Integer notNull) {
        if (1 == notNull) {
            this.notNull = 1;
        }
    }

    /** String 驼峰转下划线 */
    private static String camelToSnake(String str) {
        if (str == null || str.trim().isEmpty()) {
            return "";
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                sb.append("_");
            }
            sb.append(Character.toLowerCase(ch));
        }
        return sb.toString();
    }
}
