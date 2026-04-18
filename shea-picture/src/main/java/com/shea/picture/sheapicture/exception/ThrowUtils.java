package com.shea.picture.sheapicture.exception;

/**
 * @author : Shea.
 * @since : 2026/4/17 19:39
 */
public class ThrowUtils {

    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition,new BusinessException(errorCode));
    }

    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition,new BusinessException(errorCode, message));
    }
}
