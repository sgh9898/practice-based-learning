package com.sgh.demo.common.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * Http(s) 连接工具
 * 1. static 方法默认使用 TLSv1.2 协议
 * 2. 出现 handshake_failure 时需要切换 TLS 协议版本
 *
 * @author Song gh
 * @version 2024/11/21
 */
@Slf4j
public class OkHttpUtils {

// ------------------------------ 参数 ------------------------------

    /** 参数类型: Json */
    private static final MediaType APPLICATION_JSON = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
    /** 参数类型: 文件 */
    private static final MediaType APPLICATION_FILE = MediaType.parse(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE);
    /** 参数类型: png */
    private static final MediaType APPLICATION_PNG = MediaType.parse(org.springframework.http.MediaType.IMAGE_PNG_VALUE);
    /** 参数类型: jpeg */
    private static final MediaType APPLICATION_JPEG = MediaType.parse(org.springframework.http.MediaType.IMAGE_JPEG_VALUE);
    /** 参数类型: gif */
    private static final MediaType APPLICATION_GIF = MediaType.parse(org.springframework.http.MediaType.IMAGE_GIF_VALUE);
    /** 参数类型: Form */
    private static final MediaType APPLICATION_FORM_URLENCODED = MediaType.parse(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    /** 参数类型: 流 */
    private static final MediaType TEXT_EVENT_STREAM_VALUE = MediaType.parse(org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE);

    /** 连接超时时间(秒) */
    private static final int CONNECT_TIME_OUT = 6;
    /** 读超时时间(秒) */
    private static final int READ_TIME_OUT = 6;
    /** 写超时时间(秒) */
    private static final int WRITE_TIME_OUT = 6;

    /** sse 连接超时时间(秒) */
    private static final int SSE_CONNECT_TIME_OUT = 120;
    /** sse 读超时时间(秒) */
    private static final int SSE_READ_TIME_OUT = 120;
    /** sse 写超时时间(秒) */
    private static final int SSE_WRITE_TIME_OUT = 120;

    /** 默认协议版本 */
    private static final TLSVersion DEFAULT_TLS_VERSION = TLSVersion.TLS_V12;

    /** 信任的 host, 仅在需要时配置 */
    private static final HashSet<String> trustedHosts = null;

// ------------------------------ 构建 ------------------------------

    // 构建默认 client, 注意协议版本 TLSVersion
    private static final OkHttpClient defaultClient = new OkHttpClient.Builder()
            // 超时时间
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
//            // 信任指定 host (替换 "信任所有 host")
//            .hostnameVerifier(new StandardHostnameVerifier(trustedHosts))
            // 信任所有 host
            .hostnameVerifier((hostName, session) -> true)
            // 信任所有证书, 默认协议版本 TLSv1.2, handshake_failure 需要考虑更换使用其他协议的 client
            .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
            .build();

    // 构建 sse client, 注意协议版本 TLSVersion
    private static final OkHttpClient sseClient = new OkHttpClient.Builder(defaultClient)
            // 超时时间
            .connectTimeout(SSE_CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(SSE_READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(SSE_WRITE_TIME_OUT, TimeUnit.SECONDS)
            .build();

// ------------------------------ Get 访问 ------------------------------

    /**
     * 配置 post 请求
     *
     * @param url         接口路径
     * @param bodyJsonStr json 参数, 优先级高
     * @param params      form 参数, 优先级低
     * @param headers     header 参数
     */
    private static Request createPostRequest(@NonNull String url, String bodyJsonStr, @Nullable Map<String, Object> params, @Nullable Map<String, String> headers) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }

        RequestBody body;
        if (StringUtils.isNotBlank(bodyJsonStr)) {
            // json 参数
            body = RequestBody.create(bodyJsonStr, APPLICATION_JSON);
        } else if (params != null && !params.isEmpty()) {
            // form 参数
            body = RequestBody.create(Objects.requireNonNull(encodeValue(params, StandardCharsets.UTF_8)), APPLICATION_FORM_URLENCODED);
        } else {
            // 无参
            body = RequestBody.create("", null);
        }

        // 配置 header
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }
        return builder.post(body).build();
    }

    /** 返回 url 访问结果 (String), 协议版本 TLSv1.2 */
    private static String getStrResponse(Request request) {
        try (Response response = defaultClient.newCall(request).execute()) {
            return response.body() == null ? null : response.body().string();
        } catch (IOException e) {
            log.error("OkHttp 访问失败, 请求内容: {}", request, e);
            throw new UnsupportedOperationException("OkHttp 访问失败, url: " + request.url());
        }
    }

    /**
     * 将参数值进行编码
     *
     * @param params  参数
     * @param charset 字符集
     * @return like  a=v1&b=v2
     */
    private static String encodeValue(@NonNull Map<String, Object> params, Charset charset) {
        if (MapUtils.isNotEmpty(params)) {
            StringBuilder sb = new StringBuilder();
            for (Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                sb.append(key).append("=");
                if (value instanceof String) {
                    sb.append(URLEncoder.encode((String) value, charset)).append("&");
                } else {
                    sb.append(value).append("&");
                }
            }
            return sb.substring(0, sb.length() - 1);
        }
        return "";
    }

// ------------------------------ Post 访问 ------------------------------

    /** 信任所有证书 */
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory;
        String tlsVersionStr = "TLS";
        try {
            // 切换协议版本
            if (DEFAULT_TLS_VERSION == TLSVersion.TLS_V13) {
                tlsVersionStr = "TLSv1.3";
            }
            SSLContext sslContext = SSLContext.getInstance(tlsVersionStr);
            sslContext.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new UnsupportedOperationException("不支持 " + tlsVersionStr + " 协议");
        }
        return ssfFactory;
    }

    /** MultipartFile 转 File */
    private static File multiFiletoFile(MultipartFile multipartFile) {
        String path = "." + File.separator + multipartFile.getOriginalFilename();
        File file = new File(path);
        try {
            boolean created = file.createNewFile();
            if (!created) {
                throw new UnsupportedOperationException("文件已存在, 创建失败");
            }
            FileCopyUtils.copy(multipartFile.getBytes(), file);
        } catch (Exception e) {
            throw new UnsupportedOperationException("MultipartFile 转 File 失败");
        }
        return file;
    }

    /**
     * 信任指定 host
     *
     * @see OkHttpUtils#trustedHosts
     */
    private static class StandardHostnameVerifier implements HostnameVerifier {

        private final Set<String> trustedHosts;

        public StandardHostnameVerifier(Set<String> trustedHosts) {
            this.trustedHosts = trustedHosts;
        }

        /** 判断 host 是否在 {@link #trustedHosts} 中 */
        @Override
        public boolean verify(String hostname, SSLSession session) {
            if (trustedHosts.contains(hostname)) {
                return true;
            } else {
                HostnameVerifier verifier = HttpsURLConnection.getDefaultHostnameVerifier();
                return verifier.verify(hostname, session);
            }
        }
    }

    /** 信任所有证书 */
    private static class TrustAllCerts implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // 保持为空即可
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // 保持为空即可
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /** TLS 协议版本 */
    @Getter
    private enum TLSVersion {
        TLS_V12("TLSv12"),
        TLS_V13("TLSv13");

        private final String version;

        TLSVersion(String version) {
            this.version = version;
        }
    }

    /** get 访问 */
    public static String get(@NonNull String url) {
        return getWithHeader(url, null, null);
    }

    /** get 访问, 附带参数 */
    public static String get(@NonNull String url, @Nullable Map<String, ?> params) {
        return getWithHeader(url, params, null);
    }

    /** get 访问, 附带参数, 自定义 headers */
    public static String getWithHeader(@NonNull String url, @Nullable Map<String, ?> params, @Nullable Map<String, String> headers) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }

        // 拼接参数
        String urlWithParams;
        if (params != null && !params.isEmpty()) {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
            for (Entry<String, ?> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
            }
            urlWithParams = urlBuilder.build().toString();
        } else {
            urlWithParams = url;
        }

        // 配置 header
        Request.Builder builder = new Request.Builder().url(urlWithParams);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        return getStrResponse(request);
    }

    /** post 访问, 自定义 headers */
    public static String postWithHeaders(@NonNull String url, @NonNull Map<String, String> headers) {
        Request request = createPostRequest(url, null, null, headers);
        return getStrResponse(request);
    }

    /** post 访问, 提交 json */
    public static String postJson(@NonNull String url, String bodyJsonStr) {
        Request request = createPostRequest(url, bodyJsonStr, null, null);
        return getStrResponse(request);
    }

// ------------------------------ Private ------------------------------

    /** post 访问, 提交 json, 自定义 headers */
    public static String postJsonWithHeaders(@NonNull String url, String bodyJsonStr, @NonNull Map<String, String> headers) {
        Request request = createPostRequest(url, bodyJsonStr, null, headers);
        return getStrResponse(request);
    }

    /** post 访问, 提交 form (参数使用 UTF-8 编码) */
    public static String postForm(@NonNull String url, @NonNull Map<String, Object> params) {
        Request request = createPostRequest(url, null, params, null);
        return getStrResponse(request);
    }

    /** post 访问, 提交 form (参数使用 UTF-8 编码), 自定义 headers */
    public static String postFormWithHeaders(@NonNull String url, @NonNull Map<String, Object> params, @NonNull Map<String, String> headers) {
        Request request = createPostRequest(url, null, params, headers);
        return getStrResponse(request);
    }

    /**
     * post 访问, 提交单个本地文件 + 多个参数
     *
     * @param fileParamName 文件参数名
     * @param filePath      本地文件路径
     * @param params        常规参数
     * @param headers       header 参数
     */
    public static String postLocalFile(@NonNull String url, String fileParamName, String filePath,
                                       @Nullable Map<String, Object> params, @Nullable Map<String, String> headers) {
        File file = new File(filePath);
        return postFile(url, fileParamName, file, params, headers);
    }

    /**
     * post 访问, 提交单个文件(MultipartFile 格式) + 多个参数
     *
     * @param fileParamName 文件参数名
     * @param multipartFile 文件
     * @param params        常规参数
     * @param headers       header 参数
     */
    public static String postMultipartFile(@NonNull String url, String fileParamName, MultipartFile multipartFile,
                                           @Nullable Map<String, Object> params, @Nullable Map<String, String> headers) {
        File file = null;
        try {
            if (StringUtils.isBlank(url)) {
                throw new IllegalArgumentException("url 不能为空");
            }
            // 文件请求体
            file = multiFiletoFile(multipartFile);
            return postFile(url, fileParamName, file, params, headers);
        } finally {
            if (file != null) {
                if (!file.delete()) {
                    log.error("上传 MultipartFile 文件后, 删除临时文件失败");
                }
            }
        }
    }

    /**
     * post 访问, 提交多个文件 + 多个参数
     *
     * @param fileMap Map(文件参数名, 文件实体)
     * @param params  常规参数
     */
    public static String postFiles(@NonNull String url, Map<String, MultipartFile> fileMap, @NonNull Map<String, Object> params) {
        List<File> fileList = new LinkedList<>();
        try {
            // 添加文件
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.ALTERNATIVE);
            for (Entry<String, MultipartFile> fileEntry : fileMap.entrySet()) {
                MultipartFile multipartFile = fileEntry.getValue();
                File file = multiFiletoFile(multipartFile);
                fileList.add(file);
                RequestBody fileBody = RequestBody.create(file, APPLICATION_FILE);
                builder.addFormDataPart(fileEntry.getKey(), multipartFile.getOriginalFilename(), fileBody);
            }
            // 添加参数
            params.forEach((key, value) -> builder.addFormDataPart(key, (String) value));
            RequestBody multipartBody = builder.build();
            Request request = new Request.Builder().url(url).post(multipartBody).build();
            return getStrResponse(request);
        } finally {
            fileList.forEach(file -> {
                if (!file.delete()) {
                    log.error("批量上传 MultipartFile 文件后, 删除临时文件失败");
                }
            });
        }
    }

    /** 获取 sse 返回, 使用 post 访问, 提交 json */
    public static void ssePostJson(@NonNull String url, String bodyJsonStr, EventSourceListener eventSourceListener) {
        Request request = createPostRequest(url, bodyJsonStr, null, null);
        EventSource.Factory factory = EventSources.createFactory(sseClient);
        // 创建事件
        factory.newEventSource(request, eventSourceListener);
    }

    /** 获取 sse 返回, 使用 post 访问, 提交 json, 自定义 headers */
    public static void ssePostJsonWithHeaders(@NonNull String url, String bodyJsonStr, @NonNull Map<String, String> headers, EventSourceListener eventSourceListener) {
        Request request = createPostRequest(url, bodyJsonStr, null, headers);
        EventSource.Factory factory = EventSources.createFactory(sseClient);
        // 创建事件
        factory.newEventSource(request, eventSourceListener);
    }

    /**
     * post 访问, 提交单个文件 + 多个参数
     *
     * @param fileParamName 文件参数名
     * @param file          本地文件
     * @param params        常规参数
     * @param headers       header 参数
     */
    public static String postFile(@NonNull String url, String fileParamName, File file,
                                  @Nullable Map<String, Object> params, @Nullable Map<String, String> headers) {
        RequestBody fileBody;
        String fileName = file.getName();
        // 设置文件类型
        if (fileName.endsWith(".png")) {
            fileBody = RequestBody.create(file, APPLICATION_PNG);
        } else if (fileName.endsWith(".gif")) {
            fileBody = RequestBody.create(file, APPLICATION_GIF);
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            fileBody = RequestBody.create(file, APPLICATION_JPEG);
        } else {
            fileBody = RequestBody.create(file, APPLICATION_FILE);
        }

        // 添加文件
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(fileParamName, file.getName(), fileBody);
        // 添加参数
        if (params != null && !params.isEmpty()) {
            params.forEach((key, value) -> bodyBuilder.addFormDataPart(key, (String) value));
        }
        RequestBody multipartBody = bodyBuilder.build();

        // 配置 header
        Request.Builder headerBuilder = new Request.Builder().url(url);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(headerBuilder::addHeader);
        }

        Request request = headerBuilder.post(multipartBody).build();
        return getStrResponse(request);
    }
}
