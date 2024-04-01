package com.collin.demo.sharding.sharding.algorithm.key;

import com.collin.demo.sharding.sharding.util.SnowIdUtils;
import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;
import org.springframework.stereotype.Component;

/**
 * [Sharding 主键生成规则] 集群部署生成雪花ID
 *
 * @version 2024/3/22
 */
@Component
public class ClusterSnowflakeIdKeyGenerator implements KeyGenerateAlgorithm {

//    /** [单机环境] 自定义数据库表Id生成规则 */
//    @Override
//    public Comparable<?> generateKey() {
//        return SnowIdUtils.uniqueLong();
//    }

    /** [集群环境] 自定义数据库表Id生成规则 */
    @Override
    public Comparable<?> generateKey() {
        return SnowIdUtils.clusterUniqueLong();
    }

    /** 在配置文件中使用的算法名称 */
    @Override
    public String getType() {
        return "clusterSnowflakeId";
    }
}
