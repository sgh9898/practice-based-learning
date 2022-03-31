package com.demo.excluded;

import com.alibaba.fastjson.JSONObject;

/**
 * Elastic 插入/更新功能
 *
 * @author Song gh on 2021/12/6.
 */
public interface ElasticUploadService {
    /**
     * 在 ES 中更新或插入数据
     *
     * @param dataObj 格式见注释
     * @return 数据总数
     */
    int upsert(JSONObject dataObj);

//    /**
//     * 根据数据库表在 ES 中更新或插入数据
//     *
//     * @param sourceId model_source 表中数据源 ID, 数据仓为 8, 乐智 16
//     * @param dataObj  格式见注释
//     * @return 数据总数
//     */
//    int databaseToElastic(Long sourceId, JSONObject dataObj);
}
