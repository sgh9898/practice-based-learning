package excluded.elasticsearch;

import com.demo.util.ResultUtil;
import excluded.elasticsearch.dto.SampleIndexQueryDto;
import excluded.elasticsearch.repository.SampleIndexRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * ElasticSearch Controller
 *
 * @author Song gh on 2022/3/28.
 */
@Slf4j
@RestController
@RequestMapping("/elastic")
@Api("ElasticSearch 相关")
public class ElasticController {

    @Resource
    private SampleIndexRepository sampleIndexRepository;

    @ApiOperation("查询")
    @PostMapping("/search")
    public Map<String, Object> search(@RequestBody SampleIndexQueryDto dto) {
        return ResultUtil.success(sampleIndexRepository.pagination(dto));
    }
}
