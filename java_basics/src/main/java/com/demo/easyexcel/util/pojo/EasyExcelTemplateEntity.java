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
     * [使用默认配置时无须 override] 在默认的 BeanUtils.copyProperties 之后, 手动定义 Entity 中部分参数
     *
     * @param excel 需要转换的 Excel 类, 必须 extends {@link EasyExcelTemplateExcelVo}
     */
    public <T extends EasyExcelTemplateExcelVo> void setParamsAfterCopy(T excel) {
    }
}
