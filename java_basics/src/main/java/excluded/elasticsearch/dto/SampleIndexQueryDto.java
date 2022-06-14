package excluded.elasticsearch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "名称")
    private String name;

    @Schema(description = "代码")
    private Long code;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date endTime;

    @Schema(description = "页码")
    private int pageNum = 1;

    @Schema(description = "每页行数")
    private int pageSize = 20;
}
