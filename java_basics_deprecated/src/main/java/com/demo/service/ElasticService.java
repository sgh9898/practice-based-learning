package com.demo.service;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClientBuilder;

/**
 * Elastic 常用功能: 搜索, 删除
 *
 * @version 7.6.2
 * @author Song gh on 2021/12/17.
 */
public interface ElasticService {

    /**
     * Elastic 连接服务器时认证
     *
     * @return 用于构建 client
     */
    RestClientBuilder basicAuth();

    /**
     * Elastic 条件搜索
     *
     * @param searchObj 格式见注释
     * @return SearchResponse
     */
    SearchResponse conditionalSearch(JSONObject searchObj);

    /**
     * Elastic 删除指定数据
     *
     * @param delObj 格式见注释
     * @return 删除的数据总数
     */
    long deleteByQuery(JSONObject delObj);
}
