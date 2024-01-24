package com.demo.redis;

import com.alibaba.fastjson2.JSONObject;
import com.demo.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis
 *
 * @author Song gh on 2022/4/18.
 */
@RestController
@Api(tags = "Redis 功能")
@RequestMapping("/redis")
public class RedisController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("Redis 存入")
    @PostMapping("/redis/upsert")
    public Map<String, Object> upsertRedis(@RequestBody JSONObject json) {
        for (String key : json.keySet()) {
            stringRedisTemplate.opsForValue().setIfAbsent(key, json.getString(key), 20, TimeUnit.SECONDS);
        }
        return ResultUtil.success();
    }

    @ApiOperation("Redis 展示")
    @PostMapping("/redis/list")
    public Map<String, Object> listRedis(Set<String> keys) {
        return ResultUtil.success(stringRedisTemplate.opsForValue().get(keys));
    }
}
