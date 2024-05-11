package com.sgh.demo.common.exception;

import com.sgh.demo.common.util.ResultStatus;
import lombok.Getter;

/**
 * Json 异常
 *
 * @author Song gh on 2022/4/2.
 */
@Getter
public class JsonException extends BaseException {

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(int code, String message) {
        super(code, message);
    }

    public JsonException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public JsonException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
