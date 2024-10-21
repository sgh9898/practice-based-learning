package com.sgh.demo.common.util;

import lombok.Getter;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
 * @version 2024/8/21
 */
public class OkHttpUtils {

// ------------------------------ 参数 ------------------------------

    /** 参数类型: Json */
    private static final MediaType APPLICATION_JSON = MediaType.parse("application/json; charset=utf-8");
    /** 参数类型: 文件 */
    private static final MediaType APPLICATION_FILE = MediaType.parse("application/octet-stream");
    /** 参数类型: Form */
    private static final MediaType APPLICATION_FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded");

    /** 连接超时时间(秒) */
    private static final int CONNECT_TIME_OUT = 6;
    /** 读超时时间(秒) */
    private static final int READ_TIME_OUT = 6;
    /** 写超时时间(秒) */
    private static final int WRITE_TIME_OUT = 6;

    /** 默认协议版本 */
    private static final TLSVersion DEFAULT_TLS_VERSION = TLSVersion.TLS_V12;

    /** 信任的 host, 仅在需要时配置 */
    private static final HashSet<String> trustedHosts = null;

// ------------------------------ 构建 ------------------------------

    // 构建默认 client, 注意协议版本 TLSVersion
    private static final OkHttpClient defaultClientTLS12 = new OkHttpClient.Builder()
            // 超时时间
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
//            // 信任指定 host (替换"信任所有 host")
//            .hostnameVerifier(new StandardHostnameVerifier(trustedHosts))
            // 信任所有 host
            .hostnameVerifier((hostName, session) -> true)
            // 信任所有证书, 默认协议版本 TLSv1.2, handshake_failure 需要考虑更换使用其他协议的 client
            .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
            .build();

// ------------------------------ Get 访问 ------------------------------

    /** get 访问 */
    public static String get(@NonNull String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }
        Request request = new Request.Builder().url(url).build();
        return getStrResponse(request, url);
    }

    /** get 访问, 附带参数 */
    public static String get(@NonNull String url, @NonNull Map<String, String> params) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }

        // 拼接参数
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        params.forEach(urlBuilder::addQueryParameter);
        String urlWithParams = urlBuilder.build().toString();
        Request request = new Request.Builder().url(urlWithParams).build();
        return getStrResponse(request, url);
    }

    /** get 访问, 自定义 Headers */
    public static String getWithHeaders(@NonNull String url, @NonNull Map<String, String> headers) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }
        Request.Builder builder = new Request.Builder().url(url);
        headers.forEach(builder::addHeader);
        Request request = builder.build();
        return getStrResponse(request, url);
    }

// ------------------------------ Put 访问 ------------------------------

    /** put 访问, 自定义 Headers */
    public static String putWithHeaders(String url, String json, Map<String, String> headers) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }
        Request.Builder builder = new Request.Builder().url(url);
        headers.forEach(builder::addHeader);
        RequestBody body = RequestBody.create(json, APPLICATION_JSON);
        Request request = builder.put(body).build();
        return getStrResponse(request, url);
    }

// ------------------------------ Post 访问 ------------------------------

    /** post 访问, 自定义 Headers */
    public static String postWithHeaders(@NonNull String url, @NonNull Map<String, String> headers) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }
        RequestBody body = RequestBody.create("", null);
        Request request = new Request.Builder().url(url).post(body).build();
        return getStrResponse(request, url);
    }

    /** post 访问, 提交 json */
    public static String postJson(@NonNull String url, String bodyJsonStr) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }
        RequestBody body = RequestBody.create(bodyJsonStr, APPLICATION_JSON);
        Request request = new Request.Builder().url(url).post(body).build();
        return getStrResponse(request, url);
    }

    /** post 访问, 提交 json, 自定义 Headers */
    public static String postJsonWithHeaders(@NonNull String url, @NonNull String bodyJsonStr, @NonNull Map<String, String> headers) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }
        RequestBody body = RequestBody.create(bodyJsonStr, APPLICATION_JSON);
        Request.Builder builder = new Request.Builder().url(url);
        headers.forEach(builder::addHeader);
        Request request = builder.post(body).build();
        return getStrResponse(request, url);
    }

    /** post 访问, 提交 form (参数使用 UTF-8 编码) */
    public static String postForm(@NonNull String url, @NonNull Map<String, Object> params) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }
        RequestBody body = RequestBody.create(Objects.requireNonNull(encodeValue(params, "UTF-8")), APPLICATION_FORM_URLENCODED);
        Request request = new Request.Builder().url(url).post(body).build();
        return getStrResponse(request, url);
    }

    /** post 访问, 提交 form (参数使用 UTF-8 编码), 自定义 Headers */
    public static String postFormWithHeaders(@NonNull String url, @NonNull Map<String, Object> params, @NonNull Map<String, String> headers) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }
        RequestBody body = RequestBody.create(Objects.requireNonNull(encodeValue(params, "UTF-8")), APPLICATION_FORM_URLENCODED);
        Request.Builder builder = new Request.Builder().url(url);
        headers.forEach(builder::addHeader);
        Request request = builder.post(body).build();
        return getStrResponse(request, url);
    }

    /**
     * post 访问, 提交 form (参数使用指定字符集编码)
     *
     * @param url    接口 url
     * @param params 参数
     * @param encode 字符集, {@link java.nio.charset.StandardCharsets}
     */
    public static String postForm(@NonNull String url, @NonNull Map<String, Object> params, String encode) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url 不能为空");
        }
        RequestBody body = RequestBody.create(encodeValue(params, encode), APPLICATION_FORM_URLENCODED);
        Request request = new Request.Builder().url(url).post(body).build();
        return getStrResponse(request, url);
    }

    /**
     * post 访问, 提交单个本地文件 + 多个参数
     *
     * @param fileParamName 文件参数名
     * @param filePath      本地文件路径
     * @param params        常规参数
     */
    public static String postLocalFile(@NonNull String url, String fileParamName, String filePath, @NonNull Map<String, Object> params) {
        File file = new File(filePath);
        RequestBody fileBody = RequestBody.create(file, APPLICATION_FILE);
        // 添加文件
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.ALTERNATIVE)
                .addFormDataPart(fileParamName, file.getName(), fileBody);
        // 添加参数
        params.forEach((key, value) -> builder.addFormDataPart(key, (String) value));
        RequestBody multipartBody = builder.build();
        Request request = new Request.Builder().url(url).post(multipartBody).build();
        return getStrResponse(request, url);
    }

    /**
     * post 访问, 提交单个文件(MultipartFile 格式) + 多个参数
     *
     * @param fileParamName 文件参数名
     * @param multipartFile 文件
     * @param params        常规参数
     */
    public static String postMultipartFile(@NonNull String url, String fileParamName, MultipartFile multipartFile, @NonNull Map<String, Object> params) {
        File file = null;
        try {
            if (StringUtils.isBlank(url)) {
                throw new IllegalArgumentException("url 不能为空");
            }
            // 文件请求体
            file = multiFiletoFile(multipartFile);
            RequestBody fileBody = RequestBody.create(file, APPLICATION_FILE);
            // 添加文件
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.ALTERNATIVE)
                    .addFormDataPart(fileParamName, multipartFile.getOriginalFilename(), fileBody);
            // 添加参数
            params.forEach((key, value) -> builder.addFormDataPart(key, (String) value));
            RequestBody multipartBody = builder.build();
            Request request = new Request.Builder().url(url).post(multipartBody).build();
            return getStrResponse(request, url);
        } finally {
            if (file != null) {
                file.delete();
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
            return getStrResponse(request, url);
        } finally {
            fileList.forEach(File::delete);
        }
    }

// ------------------------------ Private ------------------------------

    /** 返回 url 访问结果 (String), 协议版本 TLSv1.2 */
    private static String getStrResponse(Request request, @NonNull String url) {
        try (Response response = defaultClientTLS12.newCall(request).execute()) {
            return response.body() == null ? null : response.body().string();
        } catch (IOException e) {
            throw new UnsupportedOperationException("OkHttp 访问失败, url: " + url);
        }
    }

    /**
     * 将参数值进行编码
     *
     * @param params 参数
     * @param encode 字符集, {@link java.nio.charset.StandardCharsets}
     * @return like  a=v1&b=v2
     */
    private static String encodeValue(@NonNull Map<String, Object> params, String encode) {
        if (MapUtils.isNotEmpty(params)) {
            StringBuilder sb = new StringBuilder();
            for (Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                sb.append(key).append("=");
                if (value instanceof String) {
                    try {
                        sb.append(URLEncoder.encode((String) value, encode)).append("&");
                    } catch (UnsupportedEncodingException e) {
                        throw new IllegalArgumentException("encode 失败：" + key + "=" + value, e);
                    }
                } else {
                    sb.append(value).append("&");
                }
            }
            return sb.substring(0, sb.length() - 1);
        }
        return "";
    }

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
}
