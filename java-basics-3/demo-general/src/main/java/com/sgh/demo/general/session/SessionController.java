package com.sgh.demo.common.session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Session 功能
 *
 * @author Song gh on 2023/3/7.
 */
@RestController
@Tag(name = "Session 功能")
@RequestMapping("/session")
public class SessionController {

    @Resource
    private com.sgh.demo.common.session.SessionService sessionService;

    @PostMapping("/create")
    @Operation(summary = "创建 session")
    public String createSession(HttpServletRequest request) {
        return sessionService.createSession(request);
    }

    @PostMapping("/read")
    @Operation(summary = "读取 session")
    public Object readSession(HttpServletRequest request) {
        return sessionService.readSession(request);
    }
}
