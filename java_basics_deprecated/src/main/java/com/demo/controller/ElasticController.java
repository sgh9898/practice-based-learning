package com.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.demo.util.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Elastic Controller
 *
 * @author Song gh on 2022/3/28.
 */
@Tag(name = "ElasticSearch Controller")
@Slf4j
@RestController
@RequestMapping("/elastic")
public class ElasticController {
}
