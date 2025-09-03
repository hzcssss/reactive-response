package io.github.hzcssss.reactive.response.core;

/**
 * 响应码枚举
 */
public enum ResponseCode {

    /**
     * 成功
     */
    SUCCESS(0, "操作成功"),

    /**
     * 失败
     */
    FAILURE(1000, "操作失败"),

    /**
     * 未授权
     */
    UNAUTHORIZED(1001, "未授权"),

    /**
     * 禁止访问
     */
    FORBIDDEN(1003, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(1004, "资源不存在"),

    /**
     * 服务器错误
     */
    ERROR(9999, "服务器错误");

    private final int errorCode;
    private final String message;

    ResponseCode(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}