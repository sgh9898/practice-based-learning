package com.demo.excluded;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.excluded.ElasticService;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Elastic 常用功能: 搜索, 删除
 *
 * @author Song gh on 2021/12/17.
 */
@Service
public class ElasticServiceImpl implements ElasticService {

    final Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private ElasticsearchProperties properties;

//    // Elastic 服务器连接配置
//    @Value("${spring.elasticsearch.rest.uris}")
//    private String ElasticServer;
//
//    @Value("${spring.elasticsearch.rest.port}")
//    private int ElasticPort;
//
//    @Value("${spring.elasticsearch.rest.username}")
//    private String ElasticUserName;
//
//    @Value("${spring.elasticsearch.rest.password}")
//    private String ElasticPassword;
//
//    @Value("${spring.elasticsearch.rest.connection-timeout}")
//    private int ConnectionTimeout;
//
//    @Value("${spring.elasticsearch.rest.read-timeout}")
//    private int ReadTimeout;


    /**
     * 连接服务器时认证
     *
     * @return 用于构建client
     */
    public RestClientBuilder basicAuth() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        // 配置用户名及密码
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword()));

        RestClientBuilder builder = RestClient.builder(new HttpHost(properties.getUris().get(0), 9200))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    // 默认发送不含验证的请求, 收到 HTTP 401 response 时才会发送验证信息
                    httpClientBuilder.disableAuthCaching();
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                });
        return builder;
    }

    /**
     * Elastic 条件搜索
     *
     * @param searchObj 格式见注释
     * @return SearchResponse
     */
    public SearchResponse conditionalSearch(JSONObject searchObj) {
        /* {
        indexName: "index1, ..."        Elastic 表名, 逗号分隔
        sort: [{name: {ASC/DESC}, ...]  排序字段
        filter:                         同 and, 不计算相关性, 快
            {
            terms: {name1: "value1, ...", ...}      匹配完整字段, 字段值逗号分隔
            wild: {name: "*value?", ...}            模糊匹配, 支持 *(空或任意字符) 和 ?(空或单个字符)  e.g. *st 匹配 test
            range: {name: {from: "...", to: "..."}} 范围匹配
            }
        must: {terms, wild, range}      同 and, 计算相关性, 慢, 结构同上
        should: {terms, wild, range}    同 or, 结构同上
        mustNot: {terms, wild, range}  同 not, 结构同上
        } */
        log.info("创建 Elastic 查询");
        ElasticsearchClient client

        // 连接 Elastic
        RestHighLevelClient client = new RestHighLevelClient(basicAuth());

        // 获取索引名(查询范围), 为空则全检索(极大影响查询效率)
        String indexName = searchObj.getString("indexName");
        if (indexName == null || indexName.isEmpty()) {
            throw new RuntimeException("indexName为空");
        }
        // 索引名以逗号分隔, 去除空格并转为 list
        String[] indexList = indexName.split(",\\s*");

        // 创建查询请求
        SearchRequest searchRequest = new SearchRequest(indexList);
        log.info("在 {} 中查询", Arrays.toString(indexList));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 添加查询条件
        // filter, 同 and, 不计算相关性, 高效
        JSONObject filterObj = searchObj.getJSONObject("filter");
        parseSearchConditions(boolQueryBuilder, "filter", filterObj);

        // must, 同 and, 计算相关性, 低效
        JSONObject mustObj = searchObj.getJSONObject("must");
        parseSearchConditions(boolQueryBuilder, "must", mustObj);

        // should, 同 or
        JSONObject shouldObject = searchObj.getJSONObject("should");
        parseSearchConditions(boolQueryBuilder, "should", shouldObject);

        // must not, 同 not
        JSONObject mustNotObject = searchObj.getJSONObject("mustNot");
        parseSearchConditions(boolQueryBuilder, "mustNot", mustNotObject);

        // 添加排序
        JSONArray sortArray = searchObj.getJSONArray("sort");
        parseSortOptions(searchSourceBuilder, sortArray);

        // 查询请求构建完成
        log.info("查询请求构建完成: {}", boolQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        // 获取结果并关闭连接
        SearchResponse searchResponse;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            log.info("查询完毕 {}", JSON.parseObject(searchResponse.toString()).getString("_shards"));
            log.info("共 {} 条结果", searchResponse.getHits().getTotalHits().value);
            client.close();
            log.info("已关闭连接");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("连接错误或执行失败");
        }

        return searchResponse;
    }

    /**
     * Elastic 删除指定数据
     *
     * @param delObj 格式见注释
     * @return 删除的数据总数
     */
    public long deleteByQuery(JSONObject delObj) {
        /* {
        indexName: "index1, ..."        Elastic 表名, 逗号分隔
        filter:                         同 and
            {
            terms: {name1: "value1, ...", ...}      匹配完整字段, 字段值逗号分隔
            wild: {name: "value?", ...}             模糊匹配, 支持 (空或任意字符) 和 ?(空或单个字符)  e.g. st 匹配 test
            range: {name: {from: "...", to: "..."}} 范围匹配
            }
        should: {terms, wild, range}    同 or, 结构同上
        mustNot: {terms, wild, range}  同 not, 结构同上
        } */
        log.info("Elastic 进行删除");

        // 连接 Elastic
        RestHighLevelClient client = new RestHighLevelClient(basicAuth());

        // 获取索引名(查询范围), 为空则全检索(极大影响查询效率)
        String indexName = delObj.getString("indexName");
        if (indexName == null || indexName.isEmpty()) {
            throw new RuntimeException("indexName为空");
        }
        // 索引名以逗号分隔, 去除空格并转为 list
        String[] indexList = indexName.split(",\\s*");

        // 创建删除请求
        DeleteByQueryRequest delRequest = new DeleteByQueryRequest(indexList);
        log.info("在 {} 中删除指定数据", Arrays.toString(indexList));
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 添加查询条件
        // filter, 同 and
        JSONObject filterObj = delObj.getJSONObject("filter");
        parseSearchConditions(boolQueryBuilder, "filter", filterObj);

        // should, 同 or
        JSONObject shouldObject = delObj.getJSONObject("should");
        parseSearchConditions(boolQueryBuilder, "should", shouldObject);

        // must not, 同 not
        JSONObject mustNotObject = delObj.getJSONObject("mustNot");
        parseSearchConditions(boolQueryBuilder, "mustNot", mustNotObject);

        // 禁止不传参, 无参数会删除当前索引全部数据
        if (filterObj == null && shouldObject == null && mustNotObject == null) {
            throw new RuntimeException("至少包含一种过滤条件");
        }

        // 删除请求构建完成
        log.info("删除请求构建完成: {}", boolQueryBuilder);
        delRequest.setQuery(boolQueryBuilder);

        // 获取结果并关闭连接
        long numDeleted;
        BulkByScrollResponse bulkResponse;
        try {
            bulkResponse = client.deleteByQuery(delRequest, RequestOptions.DEFAULT);
            numDeleted = bulkResponse.getDeleted();
            log.info("共删除 {} 条数据", numDeleted);
            client.close();
            log.info("已关闭连接");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("连接错误或执行失败");
        }

        return numDeleted;
    }

    /**
     * 解析查询条件
     *
     * @param boolQueryBuilder 构造器
     * @param condition        查询条件
     * @param conditionObj     格式见注释
     */
    private void parseSearchConditions(BoolQueryBuilder boolQueryBuilder,
                                       String condition, JSONObject conditionObj) {
        /* {
        filter: {terms: {...}, wild: {...}, range: {...}}   同 and, 不计算相关性, 快
        must: {terms, wild, range}                          同 and, 计算相关性, 慢, 结构同上
        should: {terms, wild, range}                        同 or, 结构同上
        mustNot: {terms, wild, range}                       同 not, 结构同上
        } */

        // 为空不处理
        if (conditionObj == null) {
            return;
        }

        // 解析查询条件
        List<TermsQueryBuilder> termsList = parseTerms(conditionObj.getJSONObject("terms"));
        List<WildcardQueryBuilder> wildList = parseWild(conditionObj.getJSONObject("wild"));
        List<RangeQueryBuilder> rangeList = parseRange(conditionObj.getJSONObject("range"));

        // 添加对应请求
        switch (condition) {
            case "filter":
                // 添加已解析的条件
                if (termsList != null) {
                    for (TermsQueryBuilder termsQueryBuilder : termsList) {
                        boolQueryBuilder.filter(termsQueryBuilder);
                    }
                }
                if (wildList != null) {
                    for (WildcardQueryBuilder wildcardQueryBuilder : wildList) {
                        boolQueryBuilder.filter(wildcardQueryBuilder);
                    }
                }
                if (rangeList != null) {
                    for (RangeQueryBuilder rangeQueryBuilder : rangeList) {
                        boolQueryBuilder.filter(rangeQueryBuilder);
                    }
                }
                break;
            case "must":
                // 添加已解析的条件
                if (termsList != null) {
                    for (TermsQueryBuilder termsQueryBuilder : termsList) {
                        boolQueryBuilder.must(termsQueryBuilder);
                    }
                }
                if (wildList != null) {
                    for (WildcardQueryBuilder wildcardQueryBuilder : wildList) {
                        boolQueryBuilder.must(wildcardQueryBuilder);
                    }
                }
                if (rangeList != null) {
                    for (RangeQueryBuilder rangeQueryBuilder : rangeList) {
                        boolQueryBuilder.must(rangeQueryBuilder);
                    }
                }
                break;
            case "should":
                // 添加已解析的条件
                if (termsList != null) {
                    for (TermsQueryBuilder termsQueryBuilder : termsList) {
                        boolQueryBuilder.should(termsQueryBuilder);
                    }
                }
                if (wildList != null) {
                    for (WildcardQueryBuilder wildcardQueryBuilder : wildList) {
                        boolQueryBuilder.should(wildcardQueryBuilder);
                    }
                }
                if (rangeList != null) {
                    for (RangeQueryBuilder rangeQueryBuilder : rangeList) {
                        boolQueryBuilder.should(rangeQueryBuilder);
                    }
                }
                break;
            case "mustNot":
                // 添加已解析的条件
                if (termsList != null) {
                    for (TermsQueryBuilder termsQueryBuilder : termsList) {
                        boolQueryBuilder.mustNot(termsQueryBuilder);
                    }
                }
                if (wildList != null) {
                    for (WildcardQueryBuilder wildcardQueryBuilder : wildList) {
                        boolQueryBuilder.mustNot(wildcardQueryBuilder);
                    }
                }
                if (rangeList != null) {
                    for (RangeQueryBuilder rangeQueryBuilder : rangeList) {
                        boolQueryBuilder.mustNot(rangeQueryBuilder);
                    }
                }
                break;
        }
    }

    /**
     * 解析 terms(精确匹配)
     *
     * @param termsObj terms: {name1: "value1, ...", name2...}  匹配完整字段, 逗号分隔
     */
    private List<TermsQueryBuilder> parseTerms(JSONObject termsObj) {
        // 为空不处理
        if (termsObj == null) {
            return null;
        }

        List<TermsQueryBuilder> termsList = new ArrayList<>();

        // 解析
        for (Map.Entry<String, Object> entry : termsObj.entrySet()) {
            // 字段名
            String fieldName = entry.getKey();
            // 匹配值, 多个值以逗号分割
            String value = entry.getValue().toString();
            String[] valueList = value.split(",\\s*");
            // 加入查询请求
            termsList.add(new TermsQueryBuilder(fieldName, valueList));
        }
        return termsList;
    }

    /**
     * 解析 wild(模糊匹配)
     *
     * @param wildObj wild: {name: "*value?", ...}  支持 *(空或任意字符) 和 ?(空或单个字符)  e.g. *st 匹配 test
     */
    private List<WildcardQueryBuilder> parseWild(JSONObject wildObj) {
        // 为空不处理
        if (wildObj == null) {
            return null;
        }

        List<WildcardQueryBuilder> wildList = new ArrayList<>();

        // 解析
        for (Map.Entry<String, Object> entry : wildObj.entrySet()) {
            // 字段名
            String fieldName = entry.getKey();
            // 匹配值
            String value = entry.getValue().toString();
            // 加入查询请求
            wildList.add(new WildcardQueryBuilder(fieldName, value));
        }
        return wildList;
    }

    /**
     * 解析 range(范围匹配)
     *
     * @param rangeObj range: {name: {from: "...", to: "..."}}  范围匹配
     */
    private List<RangeQueryBuilder> parseRange(JSONObject rangeObj) {
        // 为空不处理
        if (rangeObj == null) {
            return null;
        }

        List<RangeQueryBuilder> rangeList = new ArrayList<>();

        // 解析
        for (Map.Entry<String, Object> entry : rangeObj.entrySet()) {
            // 字段名
            String fieldName = entry.getKey();
            // 匹配值
            // {from: "...", to: "..."}
            JSONObject valueObj = rangeObj.getJSONObject(fieldName);
            String strFrom = valueObj.getString("from");
            String strTo = valueObj.getString("to");
            // 加入查询请求
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(fieldName);
            if (strFrom != null) {
                rangeQueryBuilder.from(strFrom);
            }
            if (strTo != null) {
                rangeQueryBuilder.to(strTo);
            }
            rangeList.add(rangeQueryBuilder);
        }
        return rangeList;
    }

    /**
     * 解析排序选项
     *
     * @param searchSourceBuilder 构造器
     * @param sortArray           排序字段
     */
    private void parseSortOptions(SearchSourceBuilder searchSourceBuilder, JSONArray sortArray) {
        // 为空不处理
        if (sortArray == null || sortArray.isEmpty()) {
            return;
        }

        // JsonArray 保证顺序
        for (int i = 0; i < sortArray.size(); i++) {
            JSONObject sortObj = sortArray.getJSONObject(i);
            // 每个排序选项只能有一个字段
            if (sortObj.entrySet().size() != 1) {
                throw new RuntimeException("JsonObj 只能包含单个排序选项");
            }
            for (Map.Entry<String, Object> entry : sortObj.entrySet()) {
                // 字段名拼接 .keyword
                String sortName = entry.getKey() + ".keyword";
                String sortOpt = entry.getValue().toString();
                if (sortOpt.equals("ASC")) {
                    searchSourceBuilder.sort(new FieldSortBuilder(sortName).order(SortOrder.ASC));
                } else if (sortOpt.equals("DESC")) {
                    searchSourceBuilder.sort(new FieldSortBuilder(sortName).order(SortOrder.DESC));
                } else {
                    throw new RuntimeException("排序须为 ASC/DESC");
                }
            }
        }
    }
}


