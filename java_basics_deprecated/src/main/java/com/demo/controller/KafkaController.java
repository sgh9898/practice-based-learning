package com.demo.controller;

import com.demo.service.KafkaService;
import com.demo.util.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Kafka
 *
 * @author Song gh on 2022/4/27.
 */
@Tag(name = "Kafka Controller", description = "Kafka 相关")
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Resource
    private KafkaService kafkaService;

    @Operation(summary = "发送消息(默认异步)")
    @PostMapping("/send")
    public Map<String, Object> sendMsg(String msg, Boolean async) {
        if (Boolean.FALSE == async) {
            return ResultUtil.success(kafkaService.synchronousSend(msg));
        } else {
            kafkaService.asynchronousSend(msg);
            return ResultUtil.success();
        }
    }
}
