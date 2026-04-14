package com.ddd.platform.common.exception;

import com.ddd.platform.common.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final Integer code;
    private final String message;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
    }

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(String message) {
        super(message);
        this.code = ErrorCode.FAIL.getCode();
        this.message = message;
    }
}