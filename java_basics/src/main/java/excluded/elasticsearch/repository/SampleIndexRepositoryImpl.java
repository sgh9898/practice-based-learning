package excluded.elasticsearch.repository;

import excluded.elasticsearch.document.SampleIndex;
import excluded.elasticsearch.dto.SampleIndexQueryDto;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * ES 演示类自定义查询
 *
 * @author Song gh on 2022/4/15.
 */
public class SampleIndexRepositoryImpl implements SampleIndexRepositoryCustom {

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 分页查询(分页参数在 dto 中)
     * 1. match 会对输入字段进行分词匹配, 如 "录用" 会被拆分为 "录", "用", 因此可匹配到 "登录"
     * 2. term 不会对输入字段进行分词匹配, 必须包含全部输入字段
     */
    @Override
    public Page<SampleIndex> pagination(SampleIndexQueryDto dto) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 精确查询 term(s) query, 效率高于 match query
        // term query 会对输入字段进行全词匹配, "测试" 可以匹配到 "测试结果"
        // match query 会对输入字段进行分词匹配, "测试" 可以匹配到 "检测"
        if (dto.getCode() != null) {
            boolQueryBuilder.filter(new TermQueryBuilder("code", dto.getCode()));
        }
        // 基于分词的模糊查询, match query
        if (dto.getName() != null) {
            boolQueryBuilder.filter(new MatchQueryBuilder("name", dto.getName()));
        }
        // 基于 regex 模糊查询, wild query
        if (dto.getName() != null) {
            // e.g. name = "*sample?"
            boolQueryBuilder.filter(new WildcardQueryBuilder("name", dto.getName()));
        }

        // 范围查询
        Date startTime = dto.getStartTime();
        Date endTime = dto.getEndTime();
        if (startTime != null || endTime != null) {
            // 转换时间格式
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("updateTime.keyword");
            if (startTime != null) {
                rangeQueryBuilder.from(format.format(startTime));
            }
            if (endTime != null) {
                rangeQueryBuilder.to(format.format(endTime));
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        // 分页
        int pageNum = dto.getPageNum();
        int pageSize = dto.getPageSize();
        pageNum = Math.max(pageNum, 1);
        pageSize = pageSize < 1 ? 20 : pageSize;
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        // 查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withSorts(new FieldSortBuilder("logTime.keyword").order(SortOrder.DESC))
                .withPageable(pageable).build();
        SearchHits<SampleIndex> response = elasticsearchRestTemplate.search(searchQuery, SampleIndex.class);
        return new PageImpl<>(response.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList()),
                pageable, response.getTotalHits());
    }

    /** 分页查询 */
    @Override
    public Page<SampleIndex> pagination(SampleIndexQueryDto dto, Pageable pageable) {
        return null;
    }
}
