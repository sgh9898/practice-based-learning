package com.collin.sharding.demo.sharding;


import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;
import org.springframework.stereotype.Component;

/**
 * Sharding--自定义数据库表Id生成规则
 *
 * @version 2024/3/22
 */
@Component
public class MyId implements KeyGenerateAlgorithm {

    /** [单机环境] 自定义数据库表Id生成规则 */
    @Override
    public Comparable<?> generateKey() {
        return SnowIdUtils.uniqueLong();
    }

//    /** [集群环境] 自定义数据库表Id生成规则 */
//    @Override
//    public Comparable<?> generateKey() {
//        return SnowIdUtils.clusterUniqueLong();
//    }

    @Override
    public String getType() {
        return "myId";
    }
}
