package com.demo.excluded;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Elastic 插入/更新功能
 *
 * @author Song gh on 2021/12/6.
 */
@Service
public class ElasticUploadServiceImpl implements ElasticUploadService {

    final Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private ElasticService elasticService;

    /**
     * 在 ES 中更新或插入数据
     *
     * @param dataObj 格式见注释
     * @return 数据总数
     */
    @Override
    public int upsert(JSONObject dataObj) {
        /* {
        indexName    Elastic 表名, 强制小写
        primaryKey   Elastic 主键, 必须在 data 中作为 key 出现
        updateOnly   true (默认) 仅更新, false 更新或插入
        dataArr: []  数据, Json Array
        } */

        String indexName = dataObj.getString("indexName");
        String primaryKey = dataObj.getString("primaryKey");
        JSONArray dataArr = dataObj.getJSONArray("dataArr");

        // 判断参数是否有效
        if (indexName == null || primaryKey == null || dataArr == null || dataArr.isEmpty()) {
            throw new RuntimeException("缺少必填字段");
        }

        // 连接 Elastic
        RestHighLevelClient client = new RestHighLevelClient(elasticService.basicAuth());

        // 创建批量请求并逐条加入数据
        BulkRequest bulkRequest = new BulkRequest(indexName);
        // 仅更新已有数据, 不插入新数据 (默认)
        if (!dataObj.containsKey("updateOnly") || dataObj.getBooleanValue("updateOnly")) {
            log.info("Elastic 不插入仅更新");
            for (int i = 0; i < dataArr.size(); i++) {
                JSONObject jsonObject = dataArr.getJSONObject(i);
                // primaryKey 存在 --> 更新
                bulkRequest.add(new UpdateRequest(indexName,
                        jsonObject.getString(primaryKey)).doc(jsonObject));
            }
        }
        // 更新已有数据, 同时插入新数据
        else {
            log.info("Elastic 插入和更新");
            for (int i = 0; i < dataArr.size(); i++) {
                JSONObject jsonObject = dataArr.getJSONObject(i);
                // primaryKey 是否存在 --> 更新/插入
                bulkRequest.add(new UpdateRequest(indexName,
                        jsonObject.getString(primaryKey)).doc(jsonObject).docAsUpsert(true));
            }
        }
        log.info("Elastic 插入或更新请求: {}", bulkRequest.getDescription());

        // 提交修改并关闭连接
        int count;
        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                log.error("Elastic 插入或更新报错: {}", bulkResponse.buildFailureMessage());
                throw new RuntimeException("未成功插入数据");
            } else {
                count = bulkResponse.getItems().length;
                log.info("Elastic 插入或更新成功, 共 {} 条数据", count);
            }
            client.close();
            log.info("已关闭连接");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("连接错误或执行失败");
        }

        return count;
    }

//    /**
//     * 根据数据库表在 ES 中更新或插入数据
//     *
//     * @param sourceId model_source 表中数据源 ID, 数据仓为 8, 乐智 16
//     * @param dataObj  格式见注释
//     * @return 数据总数
//     */
//    @Override
//    public int databaseToElastic(Long sourceId, JSONObject dataObj) {
//        /* {
//        indexName           Elastic 表名, 强制小写
//        primaryKey          Elastic 主键, 必须在 sql 中作为 key 出现
//        updateOnly          true(默认) 仅更新, false 更新或插入
//        sqlRetrieveData     sql 语句, 用于获取数据
//        } */
//
//        String indexName = dataObj.getString("indexName");
//        String primaryKey = dataObj.getString("primaryKey");
//        String sqlRetrieveData = dataObj.getString("sqlRetrieveData");
//
//        // 判断参数是否有效
//        if (indexName == null || primaryKey == null || sqlRetrieveData == null) {
//            throw new RuntimeException("缺少必填字段");
//        }
//
//        // 定位 source
//        Optional<Source> sourceOpt = sourceRepository.findById(sourceId);
//        if (!sourceOpt.isPresent() || sourceOpt.get().getIsdel() == 1) {
//            throw new RuntimeException("数据源不存在或已被删除");
//        }
//        Source source = sourceOpt.get();
//
//        // 数据库获取数据, 插入 Elastic
//        log.info("Elastic 从数据库导入");
//        JSONArray dataArr = retrieveDataFromDatabase(source, sqlRetrieveData);
//        if (dataArr.isEmpty()) {
//            log.error("未在[{}]中获取数据", source.getName());
//            throw new RuntimeException("数据库获取数据失败");
//        }
//        log.info("成功在[{}]中获取数据", source.getName());
//        dataObj.put("dataArr", dataArr);
//
//        return upsert(dataObj);
//    }
//
//    /**
//     * 在数据库中获取数据
//     *
//     * @param source          数据源 ID, 数据仓为 8, 乐智 16
//     * @param sqlRetrieveData sql语句, 用于查询
//     * @return JsonArray
//     */
//    private JSONArray retrieveDataFromDatabase(Source source, String sqlRetrieveData) {
//        // 连接数据库, 获取数据
//        DbTemplate dbTemplate;
//        List<Map<String, Object>> dataList;
//        try {
//            // 无需手动关闭
//            dbTemplate = DataSourceUtil.getDbtemplate(source.getUrl(), source.getAccount(), source.getPassword());
//            dataList = dbTemplate.getJdbcTemplate().queryForList(sqlRetrieveData);
//        } catch (DataAccessException e) {
//            log.error(e.getMessage(), e);
//            throw new RuntimeException("数据库查询失败，检查数据库或sql语句");
//        }
//
//        return JSONArray.parseArray(JSON.toJSONString(dataList));
//    }
}
