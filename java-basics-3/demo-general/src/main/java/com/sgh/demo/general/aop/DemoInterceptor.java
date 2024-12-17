package com.sgh.demo.common.aop;

import com.alibaba.fastjson2.JSONObject;
import com.sgh.demo.common.exception.BaseException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 拦截器
 *
 * @author Song gh on 2022/5/12.
 */
@Slf4j
@Component
public class DemoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) throws Exception {
        log.info("拦截器启动, uri: {}", request.getRequestURI());

        // 获取 parameter
        String msg = request.getParameter("message");

        // 获取 json
        String bodyStr = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(msg) && StringUtils.isBlank(bodyStr)) {
            throw new BaseException("Interceptor 报错: 无数据传入");
        }

        // 处理 request body
        JSONObject processed = new JSONObject();
        processed.put("originalMsg", msg);
        processed.put("jsonMsg", bodyStr);
        processed.put("interceptorAdded", "interceptor 添加的信息");
        request.setAttribute("processedMsg", processed);

        log.info("拦截器完成, uri: {}", request.getRequestURI());
        return true;
    }
}
