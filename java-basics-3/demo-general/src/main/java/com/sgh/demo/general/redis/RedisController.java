package com.sgh.demo.common.redis;

import com.alibaba.fastjson2.JSONObject;
import com.sgh.demo.common.util.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis
 *
 * @author Song gh on 2022/4/18.
 */
@RestController
@Tag(name = "Redis 功能")
@RequestMapping("/redis")
public class RedisController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "Redis 存入")
    @PostMapping("/redis/upsert")
    public Map<String, Object> upsertRedis(@RequestBody JSONObject json) {
        for (String key : json.keySet()) {
            stringRedisTemplate.opsForValue().setIfAbsent(key, json.getString(key), 20, TimeUnit.SECONDS);
        }
        return ResultUtil.success();
    }

    @Operation(summary = "Redis 展示")
    @PostMapping("/redis/list")
    public Map<String, Object> listRedis(Set<String> keys) {
        return ResultUtil.success(stringRedisTemplate.opsForValue().get(keys));
    }
}
