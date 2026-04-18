package com.shea.picture.sheapicture.common;

import com.shea.picture.sheapicture.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shea.
 * @since : 2026/4/17 19:31
 */
@Data
public class Result<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(int code,String message,T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(data);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> success(T data) {
        return success(200,"ok",data);
    }

    public static <T> Result<T> success(int code,String message) {
        return success(code,message,null);
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> Result<T> error(ErrorCode errorCode, String message) {
        return error(errorCode.getCode(), message);
    }

    public static <T> Result<T> error(int code,String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
