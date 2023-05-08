package excluded.elasticsearch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.util.Date;

/**
 * ES 演示类查询用 dto
 *
 * @author Song gh on 2022/4/15.
 */
@Data
@Document(indexName = "sample_index")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleIndexQueryDto {

    @Id
    private String id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("代码")
    private Long code;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty("页码")
    private int pageNum = 1;

    @ApiModelProperty("每页行数")
    private int pageSize = 20;
}
