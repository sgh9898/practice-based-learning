//package com.demo.util;
//
//import com.alibaba.fastjson2.JSONObject;
//import com.dingtalk.api.DefaultDingTalkClient;
//import com.dingtalk.api.DingTalkClient;
//import com.dingtalk.api.request.OapiGettokenRequest;
//import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
//import com.dingtalk.api.response.OapiGettokenResponse;
//import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
//import com.taobao.api.ApiException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpMethod;
//
//import java.util.List;
//import java.util.Objects;
//
/// **
// * 钉钉相关工具类
// *
// * @author Song gh on 2022/11/17.
// */
//@Slf4j
//public class DingDingUtil {
//
//    // ------------------------------ 不可变参数 ------------------------------
//    /** 消息类型 - 文本 */
//    public static final String MSG_TYPE_TEXT = "text";
//
//    /** url - 获取 accessToken */
//    public static final String GET_ACCESS_TOKEN_URL = "https://oapi.dingtalk.com/gettoken";
//    /** url - 异步发送信息 */
//    public static final String ASYNC_SEND_MESSAGE = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";
//
//    // ------------------------------ 可变参数 ------------------------------
//    @Value("${dingtalk.app_key}")
//    private static String appKey;
//    @Value("${dingtalk.app_secret}")
//    private static String appSecret;
//    @Value("${dingtalk.app_agent_id}")
//    private static Long appAgentId;
//
//    /**
//     * 获取 accessToken
//     * <br> 1. 有效期 2h
//     * <br> 2. 多个应用的 accessToken 彼此独立
//     * <br> 3. 不可频繁调用, 会受到频率拦截
//     */
//    public static String getAccessToken() {
//        // 创建 client
//        DefaultDingTalkClient client = new DefaultDingTalkClient(GET_ACCESS_TOKEN_URL);
//        OapiGettokenRequest request = new OapiGettokenRequest();
//        // 填写参数
//        request.setAppkey(appKey);
//        request.setAppsecret(appSecret);
//        request.setHttpMethod(HttpMethod.GET.name());
//        try {
//            OapiGettokenResponse response = client.execute(request);
//            if (!Objects.isNull(response)) {
//                return response.getAccessToken();
//            } else {
//                log.error("获取 accessToken 响应为空");
//            }
//        } catch (ApiException e) {
//            log.error("获取 accessToken 访问失败", e);
//        }
//        return null;
//    }
//
//    /**
//     * 以文本形式发送通知
//     *
//     * @param msgContent 消息内容
//     * @param userIdList 接收者的 userId list
//     * @return taskId 本条任务的 id
//     */
//    public static Long sendTextNotification(String msgContent, List<String> userIdList, String accessToken) {
//        // 创建 client
//        DingTalkClient client = new DefaultDingTalkClient(ASYNC_SEND_MESSAGE);
//        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
//
//        // 构建文本消息内容
//        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
//        msg.setMsgtype(MSG_TYPE_TEXT);
//        OapiMessageCorpconversationAsyncsendV2Request.Text text = new OapiMessageCorpconversationAsyncsendV2Request.Text();
//        text.setContent(msgContent);
//        msg.setText(text);
//
//        // 填写参数
//        request.setAgentId(appAgentId);
//        request.setUseridList(String.join(",", userIdList));
//        // 默认不发送给所有人
//        request.setToAllUser(Boolean.FALSE);
//        request.setMsg(msg);
//
//        try {
//            log.debug("钉钉文本消息推送: {}", JSONObject.toJSONString(request));
//            OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, accessToken);
//            log.info("钉钉文本消息推送结果: {}", JSONObject.toJSONString(response));
//            if (response.isSuccess()) {
//                // 当前推送任务对应的 taskId
//                return response.getTaskId();
//            } else {
//                log.error("钉钉文本消息推送报错 code: {}, msg: {}", response.getErrorCode(), response.getErrmsg());
//                throw new RuntimeException();
//            }
//        } catch (ApiException e) {
//            log.error("钉钉文本消息推送失败", e);
//            throw new RuntimeException();
//        }
//    }
//}
