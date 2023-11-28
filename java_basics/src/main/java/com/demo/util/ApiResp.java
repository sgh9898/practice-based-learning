package com.demo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一的接口返回格式
 * 1. 常规接口, 返回格式 {@link ApiResp}
 * 2. 实体类单条数据, 返回格式 {@link Entity}
 * 3. 实体类列表数据, 返回格式 {@link ListEntity}
 * 2. 实体类分页数据, 返回格式 {@link PageEntity}
 *
 * @author Song gh on 2023/11/24.
 */
@Data
@JsonPropertyOrder({"code", "message"})
public class ApiResp {

// ------------------------------ 参数 ------------------------------
    /** 状态码, {@link ResultStatus#getCode()} */
    private int code;

    /** 返回信息 or 报错描述, {@link ResultStatus#getMessage()} */
    private String message;

    /** 自定义数据, null 时不展示 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

// ------------------------------ 构造 ------------------------------

    /** [构造] 使用已有的 code, message */
    public ApiResp(ResultStatus resultStatus) {
        this.code = resultStatus.getCode();
        this.message = resultStatus.getMessage();
    }

    /** [构造] 自定义 code, message */
    public ApiResp(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /** [构造] 自定义 code, message, data */
    public ApiResp(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

// ------------------------------ 通用返回方法 ------------------------------

    /** 成功 */
    public static ApiResp success() {
        return new ApiResp(ResultStatus.SUCCESS.getCode(), ResultStatus.SUCCESS.getMessage(), null);
    }

    /** 成功: 使用 "data" 字段存放数据 */
    public static ApiResp success(Object data) {
        return new ApiResp(ResultStatus.SUCCESS.getCode(), ResultStatus.SUCCESS.getMessage(), data);
    }

    /** 成功: 合并 code, message, 以及自定义 map 数据 */
    public static Map<String, Object> success(Map<String, Object> dataMap) {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("code", ResultStatus.SUCCESS.getCode());
        returnMap.put("message", ResultStatus.SUCCESS.getMessage());
        returnMap.putAll(dataMap);
        return returnMap;
    }

    /** 失败 */
    public static ApiResp error() {
        return new ApiResp(ResultStatus.ERROR.getCode(), ResultStatus.ERROR.getMessage(), null);
    }

// ------------------------------ 实体类, 单条数据 ------------------------------

    /** [实体, 单条] 返回单条实体类数据 */
    @Getter
    public static class Entity<T> extends ApiResp {

        /** 数据格式 */
        private final T data;

        public Entity(T data) {
            super(ResultStatus.SUCCESS);
            this.data = data;
        }
    }

// ------------------------------ 实体类, 列表数据 ------------------------------

    /** [实体, 列表] 返回实体类列表数据 */
    @Getter
    public static class ListEntity<T> extends ApiResp {

        /** 数据格式 */
        private final Collection<T> data;

        public ListEntity(Collection<T> data) {
            super(ResultStatus.SUCCESS);
            this.data = data;
        }
    }

// ------------------------------ 实体类, 分页数据 ------------------------------

    /** [实体, 分页] 返回实体类分页数据 */
    @Getter
    public static class PageEntity<T> extends ApiResp {

        /** 总数据量 */
        private final Integer totalNum;

        /** 总页码 */
        private final Integer totalPage;

        /** 数据格式 */
        List<T> data;

        public PageEntity(Page<T> pageResult) {
            super(ResultStatus.SUCCESS);
            this.data = pageResult.getContent();
            this.totalNum = Math.toIntExact(pageResult.getTotalElements());
            this.totalPage = pageResult.getTotalPages();
        }

        public PageEntity(List<T> dataCollection, Integer totalNum, Integer totalPage) {
            super(ResultStatus.SUCCESS);
            this.data = dataCollection;
            this.totalNum = totalNum;
            this.totalPage = totalPage;
        }
    }
}
