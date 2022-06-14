package excluded.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "ES 演示类")
@Document(indexName = "sample_index")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleIndex {

    @Id
    private String id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "代码")
    private Long code;

    @Schema(description = "更新时间")
    private String updateTime;

    @Schema(description = "系统时间")
    private Date sysTime;
}
