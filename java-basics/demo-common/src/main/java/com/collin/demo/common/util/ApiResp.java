package com.collin.demo.common.util;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一的接口返回格式
 * <br> (便于 OpenAPI 自动生成接口文档时解析数据结构)
 * <br> 1. 常规接口(无数据), 返回格式 {@link ApiResp}
 * <br> 2. 常规接口(有数据), 返回格式 {@link Data}
 * <br> 3. 实体类(单条数据), 返回格式 {@link Entity}
 * <br> 4. 实体类(列表数据), 返回格式 {@link ListEntity}
 * <br> 5. 实体类(分页数据), 返回格式 {@link PageEntity}
 *
 * @author Song gh on 2023/11/24.
 */
@Getter
@JsonPropertyOrder({"code", "message"})
public class ApiResp {

// ------------------------------ 参数 ------------------------------
    /** 状态码, {@link ResultStatus#getCode()} */
    private final int code;

    /** 返回信息 or 报错描述, {@link ResultStatus#getMessage()} */
    private final String message;

// ------------------------------ 构造 ------------------------------

    /** [构造] 自定义 code, message */
    protected ApiResp(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /** [构造] 使用已有的 code, message */
    protected ApiResp(ResultStatus resultStatus) {
        this.code = resultStatus.getCode();
        this.message = resultStatus.getMessage();
    }

// ------------------------------ 通用返回方法 ------------------------------

    /** 成功 */
    public static ApiResp success() {
        return new ApiResp(ResultStatus.SUCCESS);
    }

    /** 成功: 在最外层(code, message 层)添加自定义 map 数据 */
    public static Map<String, Object> success(Map<String, Object> dataMap) {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("code", ResultStatus.SUCCESS.getCode());
        returnMap.put("message", ResultStatus.SUCCESS.getMessage());
        returnMap.putAll(dataMap);
        return returnMap;
    }

    /** 失败 */
    public static ApiResp error() {
        return new ApiResp(ResultStatus.ERROR);
    }

    /** 失败: 附带自定义 message */
    public static ApiResp error(String message) {
        return new ApiResp(ResultStatus.ERROR.getCode(), message);
    }

    /** 自定义返回: 手动设置 code, message */
    public static ApiResp info(Integer code, String message) {
        return new ApiResp(code, message);
    }

// ------------------------------ 通用返回, 附带数据 ------------------------------

    /** [通用] 返回时附带数据 */
    @Getter
    public static class Data extends ApiResp {

        /** 返回数据, 格式不限 */
        private final Object data;

        public Data(Object data) {
            super(ResultStatus.SUCCESS);
            this.data = data;
        }
    }

// ------------------------------ 实体类, 单条数据 ------------------------------

    /** [实体, 单条] 返回单条实体类数据 */
    @Getter
    public static class Entity<T> extends ApiResp {

        /** 实体类单条数据 */
        private final T data;

        /** [构造] 实体类单条数据 */
        public Entity(T data) {
            super(ResultStatus.SUCCESS);
            this.data = data;
        }
    }

// ------------------------------ 实体类, 列表数据 ------------------------------

    /** [实体, 列表] 返回实体类列表数据 */
    @Getter
    public static class ListEntity<T> extends ApiResp {

        /** 实体类列表数据 */
        private final Collection<T> data;

        /** [构造] 实体类列表数据 */
        public ListEntity(Collection<T> data) {
            super(ResultStatus.SUCCESS);
            this.data = data;
        }
    }

// ------------------------------ 实体类, 分页数据 ------------------------------

    /** [实体, 分页] 返回实体类分页数据 */
    @Getter
    public static class PageEntity<T> extends ApiResp {

        /** 实体类列表数据 */
        private final List<T> data;

        /** 总数据量 */
        private final Integer totalNum;

        /** 总页码 */
        private final Integer totalPage;

        /** [构造] 实体类分页数据 */
        public PageEntity(Page<T> pageResult) {
            super(ResultStatus.SUCCESS);
            this.data = pageResult.getContent();
            this.totalNum = Math.toIntExact(pageResult.getTotalElements());
            this.totalPage = pageResult.getTotalPages();
        }

        /** [构造] 自定义实体类分页数据 */
        public PageEntity(List<T> listData, Integer totalNum, Integer totalPage) {
            super(ResultStatus.SUCCESS);
            this.data = listData;
            this.totalNum = totalNum;
            this.totalPage = totalPage;
        }

        /** [构造] 自定义实体类分页数据 */
        public PageEntity(List<T> listData, Long totalNum, Integer totalPage) {
            super(ResultStatus.SUCCESS);
            this.data = listData;
            this.totalNum = Math.toIntExact(totalNum);
            this.totalPage = totalPage;
        }
    }
}
