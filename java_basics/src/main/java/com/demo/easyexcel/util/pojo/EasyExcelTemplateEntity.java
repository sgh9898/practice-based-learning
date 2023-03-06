package com.demo.easyexcel.util.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Excel 对应 Entity 类, 用于 Excel[导入/导出]
 *
 * @author Song gh on 2023/02/28.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class EasyExcelTemplateEntity {

    /**
     * 将 Excel 类转换为 Entity 类
     *
     * @param excel 需要转换的 Excel 类, 必须 extends {@link EasyExcelTemplateExcelVo}
     * @return 转换后的 Entity 类, 必须 extends 本类
     */
    public abstract <T extends EasyExcelTemplateEntity, U extends EasyExcelTemplateExcelVo> T convertExcel(U excel);
}
