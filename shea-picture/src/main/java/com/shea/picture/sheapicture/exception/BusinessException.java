package com.shea.picture.sheapicture.exception;

import lombok.Getter;

/**
 * 业务异常
 * @author : Shea.
 * @since : 2026/4/17 19:36
 */
@Getter
public class BusinessException extends RuntimeException{

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode code) {
        super(code.getMsg());
        this.code = code.getCode();
    }

    public BusinessException(ErrorCode code,String message) {
        super(message);
        this.code = code.getCode();
    }
}
