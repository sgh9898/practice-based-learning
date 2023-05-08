package excluded.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.util.Date;

/**
 * ES 演示类
 *
 * @author Song gh on 2022/4/15.
 */
@Data
@ApiModel("ES 演示类")
@Document(indexName = "sample_index")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleIndex {

    @Id
    private String id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("代码")
    private Long code;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("系统时间")
    private Date sysTime;
}
