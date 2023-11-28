package com.demo.db.controller;

import com.demo.async.AsyncService;
import com.demo.db.entity.DemoEntity;
import com.demo.db.pojo.ComplaintsSearchDto;
import com.demo.db.repository.DemoEntityRepository;
import com.demo.util.JacksonUtils;
import com.demo.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 基础或未分类功能
 *
 * @author Song gh on 2022/5/18.
 */
@Slf4j
@Controller
@Api(tags = "基础或未分类功能")
@RequestMapping("/general")
public class GeneralController {

    @Resource
    private DemoEntityRepository demoEntityRepository;

    @Resource
    private AsyncService asyncService;

    @ApiOperation("异步任务")
    @PostMapping("/async")
    public Map<String, Object> demoAsync() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();

        Future<Boolean> async1 = asyncService.asyncTask("1", 1);
        Future<Boolean> async2 = asyncService.asyncTask("2", 3);
        Future<Boolean> async3 = asyncService.asyncTask("3", 5);

        // 调用 get() 阻塞主线程
        async1.get();
        async2.get();
        async3.get();

        long time = System.currentTimeMillis() - start;
        log.info("异步任务总耗时: {}", time);
        return ResultUtil.success();
    }

    @ApiOperation("同步任务")
    @PostMapping("/sync")
    public Map<String, Object> demoSync() throws InterruptedException {
        long start = System.currentTimeMillis();

        asyncService.syncTask("1", 1);
        asyncService.syncTask("2", 3);
        asyncService.syncTask("3", 5);

        long time = System.currentTimeMillis() - start;
        log.info("同步任务总耗时: {}", time);
        return ResultUtil.success();
    }

    @ApiOperation("分页查询测试")
    @GetMapping("/pagination")
    public Map<String, Object> pagination(String name, Pageable page) {
        return ResultUtil.success(demoEntityRepository.findAllByName(name, page));
    }

    @ApiOperation("存储")
    @PostMapping("/save")
    public Map<String, Object> save(DemoEntity demoEntity) {
        demoEntity.setIsDeleted(false);
        demoEntityRepository.save(demoEntity);
        return ResultUtil.success();
    }

    @ApiOperation("转发请求(相同 host, port)")
    @PostMapping("/forward")
    public void okhttp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String body = "{\n" +
                "    \"validType\": 1\n" +
                "}";
        request.setAttribute("searchDto", body);
        request.getRequestDispatcher("http://localhost:8071/accept-sm-api/api/acceptSm/excel/exportData").forward(request, response);
    }

    /** 此时不可使用 @RestController, 而是要使用 @Controller, 否则重定向不生效 */
    @ApiOperation("重定向(不同 host, port)")
    @PostMapping("/redirect")
    public String okhttp1(RedirectAttributes attributes, @RequestBody ComplaintsSearchDto demo) {
//        String body = "{\"validType\": 1}";
        attributes.addAttribute("searchDto", JacksonUtils.beanToJson(demo));
        String url = "http://localhost:8071/accept-sm-api/api/acceptSm/excel/exportData";
        return "redirect:" + url;
    }
}
