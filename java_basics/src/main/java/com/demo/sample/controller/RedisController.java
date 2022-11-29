package com.demo.sample.controller;

import com.alibaba.fastjson.JSONObject;
import com.demo.util.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/redis")
@Tag(name = "Redis Controller", description = "Redis 相关")
public class RedisController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "Redis 存入")
    @PostMapping("/redis/upsert")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject("{key : value}")}))
    public Map<String, Object> upsertRedis(@RequestBody JSONObject json) {
        for (String key : json.keySet()) {
            stringRedisTemplate.opsForValue().set(key, json.getString(key), 20, TimeUnit.SECONDS);
        }
        return ResultUtil.success();
    }

    @Operation(summary = "Redis 展示")
    @PostMapping("/redis/list")
    public Map<String, Object> listRedis(Set<String> keys) {
        return ResultUtil.success(stringRedisTemplate.opsForValue().get(keys));
    }
}
