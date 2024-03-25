package com.collin.demo.sharding.sharding;


import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;
import org.springframework.stereotype.Component;

/**
 * Sharding--自定义数据库表Id生成规则
 */
@Component
public class MyId implements KeyGenerateAlgorithm {

    /** [集群环境] 自定义数据库表Id生成规则 */
    @Override
    public Comparable<?> generateKey() {
        return SnowIdUtils.clusterUniqueLong();
    }

//    /** 自定义数据库表Id生成规则 */
//    @Override
//    public Comparable<?> generateKey() {
//        return SnowIdUtils.uniqueLong();
//    }

    @Override
    public String getType() {
        return "myId";
    }
}
