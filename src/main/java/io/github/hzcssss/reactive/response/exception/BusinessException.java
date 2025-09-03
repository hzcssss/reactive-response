package io.github.hzcssss.reactive.response.exception;

import io.github.hzcssss.reactive.response.core.ResponseCode;

/**
 * 业务异常类
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final int errorCode;

    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        this(ResponseCode.FAILURE.getErrorCode(), message);
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param message   错误消息
     */
    public BusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param responseCode 响应码枚举
     */
    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.errorCode = responseCode.getErrorCode();
    }

    /**
     * 构造函数
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public BusinessException(String message, Throwable cause) {
        this(ResponseCode.FAILURE.getErrorCode(), message, cause);
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param cause     原始异常
     */
    public BusinessException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * 获取错误码（兼容旧版本）
     *
     * @return 错误码
     * @deprecated 使用 {@link #getErrorCode()} 代替
     */
    @Deprecated
    public int getCode() {
        return errorCode;
    }
}