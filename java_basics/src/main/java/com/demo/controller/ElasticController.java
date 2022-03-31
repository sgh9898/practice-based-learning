package com.demo.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Elastic Controller
 *
 * @author Song gh on 2022/3/28.
 */
@Api(tags = "ElasticSearch Controller")
@RestController
@RequestMapping("/elastic")
public class ElasticController {
    private static final Logger log = LoggerFactory.getLogger(ElasticController.class);
}
