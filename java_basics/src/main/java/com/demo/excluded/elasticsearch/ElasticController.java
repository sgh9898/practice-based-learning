package com.demo.elasticsearch;

import com.demo.elasticsearch.document.SampleIndex;
import com.demo.elasticsearch.dto.SampleIndexQueryDto;
import com.demo.elasticsearch.repository.SampleIndexRepository;
import com.demo.util.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "ElasticSearch Controller", description = "ElasticSearch 相关")
@Slf4j
@RestController
@RequestMapping("/elastic")
public class ElasticController {

    private static final String apiResponse = "{\"code\":0,\"message\":\"成功\",\"data\":list}";

    @Resource
    private SampleIndexRepository sampleIndexRepository;

    @Operation(summary = "查询")
    @PostMapping("/search")
    @ApiResponse(responseCode = "200", description = apiResponse, content = {@Content(schema = @Schema(allOf = SampleIndex.class))})
    public Map<String, Object> search(@RequestBody SampleIndexQueryDto dto) {
        return ResultUtil.success(sampleIndexRepository.pagination(dto));
    }
}
