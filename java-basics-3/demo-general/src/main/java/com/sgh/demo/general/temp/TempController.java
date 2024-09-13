//package com.sgh.demo.common.temp;
//
//import com.sgh.demo.common.async.AsyncService;
//import com.sgh.demo.common.database.entity.db.DemoEntity;
//import com.sgh.demo.common.constant.ApiResp;
//import com.sgh.demo.common.util.JsonUtils;
//import com.sgh.demo.common.util.ResultUtil;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import jakarta.annotation.Resource;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//
///**
// * 临时功能/测试
// *
// * @author Song gh on 2022/5/18.
// */
//@Slf4j
//@RestController
//@Tag(name = "临时功能/测试")
//@RequestMapping("/general")
//public class TempController {
//
//    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
//
//    @Resource
//    private AsyncService asyncService;
//
//    @Operation(summary = "异步任务")
////    @PostMapping("/sseTest")
//    public ApiResp.Entity<DemoEntity> sseTest(@RequestBody DemoEntity demoEntity) throws InterruptedException, ExecutionException {
//        return new ApiResp.Entity<>(demoEntity);
//    }
//
//    public String postEventStream(String urlStr, String json, HttpServletResponse response) {
//        long statr = System.currentTimeMillis();
//        log.info("开始请求接口url:{},请求参数{}", urlStr,json);
//        InputStream is = null;
//        //11.读取输入流中的返回值
//        StringBuffer bu = new StringBuffer();
//        try {
//            //1.设置URL
//            URl url = new URL(urlStr);
//            //2.打开URL连接
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            //3.设置请求方式
//            conn.setRequestMethod("POST");
//            //4.设置Content-Type
//            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
//            //5.设置Accept
//            conn.setRequestProperty("Accept", "text/event-stream");
//            //6.设置DoOutput
//            conn.setDoOutput(true);
//            //7.设置DoInput
//            conn.setDoInput(true);
//            //8.获取输出流
//            OutputStream os = conn.getOutputStream();
//            //9.写入参数（json格式）
//            os.write(json.getBytes("utf-8"));
//            os.flush();
//            os.close();
//            //10.获取输入流
//            is = conn.getInputStream();
//
//            byte[] bytes = new byte[1024];
//            int len = 0;
//            long end = System.currentTimeMillis();
//            log.info("接口url:{},请求参数{},请求开始流式输出{}", urlStr,json, end - statr);
//            while ((len = is.read(bytes)) != -1) {
//                String line = new String(bytes, 0, len, "utf-8");
//
//                response.getWriter().write(line);
//                response.getWriter().flush();
//                bu.append(line);
//            }
//        } catch (IOException e) {
//            log.error("请求模型接口异常", e);
//            throw new BusinessException(ResponseCode.TOPIC_INITIATION_FAILED);
//        } finally {
//            if (!Objects.isNull(is)) {
//                try {
//                    //12.关闭输入流
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//        return bu.toString();
//
//    }
//
//
//    @PostMapping("/sseTest")
//    public SseEmitter handleSse(@RequestBody DemoEntity demoEntity) throws InterruptedException {
//        SseEmitter emitter = new SseEmitter();
//        try {
//            emitter.send(SseEmitter.event().data(demoEntity.getName()));
//            Thread.sleep(3000);
//            emitter.send(SseEmitter.event().data(demoEntity.getComment()));
//        } catch (IOException e) {
//            emitter.complete();
//            emitters.remove(emitter);
//        }
//
//        emitter.onCompletion(() -> emitters.remove(emitter));
//        emitter.onTimeout(() -> emitters.remove(emitter));
//
//        return emitter;
//    }
//
//    public void sendSseMessage(DemoEntity demoEntity) throws InterruptedException {
//        for (SseEmitter emitter : emitters) {
//            try {
//                emitter.send(SseEmitter.event().data(demoEntity.getName()));
//                emitter.send(SseEmitter.event().data(demoEntity.getComment()));
//            } catch (IOException e) {
//                emitter.complete();
//                emitters.remove(emitter);
//            }
//        }
//    }
//
//    @Operation(summary = "异步任务")
//    @PostMapping("/async")
//    public Map<String, Object> demoAsync() throws InterruptedException, ExecutionException {
//        long start = System.currentTimeMillis();
//
//        Future<Boolean> async1 = asyncService.asyncTask("1", 1);
//        Future<Boolean> async2 = asyncService.asyncTask("2", 3);
//        Future<Boolean> async3 = asyncService.asyncTask("3", 5);
//
//        // 调用 get() 阻塞主线程
//        async1.get();
//        async2.get();
//        async3.get();
//
//        long time = System.currentTimeMillis() - start;
//        log.info("异步任务总耗时: {}", time);
//        return ResultUtil.success();
//    }
//
//    @Operation(summary = "同步任务")
//    @PostMapping("/sync")
//    public Map<String, Object> demoSync() throws InterruptedException {
//        long start = System.currentTimeMillis();
//
//        asyncService.syncTask("1", 1);
//        asyncService.syncTask("2", 3);
//        asyncService.syncTask("3", 5);
//
//        long time = System.currentTimeMillis() - start;
//        log.info("同步任务总耗时: {}", time);
//        return ResultUtil.success();
//    }
//
//    @Operation(summary = "转发请求(相同 host, port)")
//    @PostMapping("/forward")
//    public void okhttp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String body = "{\n" +
//                "    \"validType\": 1\n" +
//                "}";
//        request.setAttribute("searchDto", body);
//        request.getRequestDispatcher("http://localhost:8071/accept-sm-api/api/acceptSm/excel/exportData").forward(request, response);
//    }
//
////    /** 此时不可使用 @RestController, 而是要使用 @Controller, 否则重定向不生效 */
////    @Operation(summary = "重定向(不同 host, port)")
////    @PostMapping("/redirect")
////    public String okhttp1(RedirectAttributes attributes, @RequestBody ComplaintsSearchDto demo) {
//////        String body = "{\"validType\": 1}";
//////        attributes.addFlashAttribute("searchDto", body);
////        String url = "http://localhost:8071/accept-sm-api/api/acceptSm/excel/exportData";
////        return OkHttpUtils.get(url);
////    }
//}
