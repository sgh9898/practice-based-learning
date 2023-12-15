package com.demo.session;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Session 功能
 *
 * @author Song gh on 2023/3/7.
 */
@RestController
@Api(tags = "Session 功能")
@RequestMapping("/session")
public class SessionController {

    @Resource
    private SessionService sessionService;

    @PostMapping("/create")
    @ApiOperation("创建 session")
    public String createSession(HttpServletRequest request) {
        return sessionService.createSession(request);
    }

    @PostMapping("/read")
    @ApiOperation("读取 session")
    public Object readSession(HttpServletRequest request) {
        return sessionService.readSession(request);
    }
}
