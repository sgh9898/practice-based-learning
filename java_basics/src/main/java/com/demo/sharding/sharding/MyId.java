package com.demo.sharding.sharding;


import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * Sharding--自定义数据库表Id生成规则
 */
@Component
public class MyId implements KeyGenerateAlgorithm {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /** [redis 前缀] 用于集群情况下查重 */
    private static final String SNOWFLAKE_REDIS_PREFIX = "SNOWFLAKE_ID:";

    /** 自定义数据库表Id生成规则 */
    @Override
    public Comparable<?> generateKey() {
        long snowflakeId = SnowIdUtils.uniqueLong();
        String redisKey = SNOWFLAKE_REDIS_PREFIX + snowflakeId;
        while (StringUtils.isNotBlank(stringRedisTemplate.opsForValue().get(redisKey))) {
            snowflakeId = SnowIdUtils.uniqueLong();
        }
        stringRedisTemplate.opsForValue().set(redisKey, "1", Duration.ofSeconds(1));
        return snowflakeId;
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
