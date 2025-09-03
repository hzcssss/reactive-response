package io.github.hzcssss.reactive.response.core;

import io.github.hzcssss.reactive.response.exception.BusinessException;

import java.io.Serializable;

/**
 * 响应式编程场景下的通用响应包装类
 *
 * @param <T> 响应数据类型
 */
public class ReactiveResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务错误码，默认为0，用4位表示
     */
    private int errorCode;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 构造函数
     */
    public ReactiveResponse() {
        this.errorCode = 0; // 默认错误码为0
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 创建成功响应
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> ReactiveResponse<T> success() {
        return success(null);
    }

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 响应对象
     */
    public static <T> ReactiveResponse<T> success(T data) {
        return success(ResponseCode.SUCCESS.getErrorCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    /**
     * 创建成功响应
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> ReactiveResponse<T> success(String message, T data) {
        return success(0, message, data);
    }

    /**
     * 创建成功响应
     *
     * @param errorCode 业务错误码
     * @param message   响应消息
     * @param data      响应数据
     * @param <T>       数据类型
     * @return 响应对象
     */
    public static <T> ReactiveResponse<T> success(int errorCode, String message, T data) {
        ReactiveResponse<T> response = new ReactiveResponse<>();
        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setData(data);
        response.setSuccess(true);
        return response;
    }

    /**
     * 创建失败响应
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> ReactiveResponse<T> failure() {
        return failure(ResponseCode.FAILURE.getErrorCode(), ResponseCode.FAILURE.getMessage());
    }

    /**
     * 创建失败响应
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> ReactiveResponse<T> failure(String message) {
        return failure(ResponseCode.FAILURE.getErrorCode(), message);
    }

    /**
     * 创建失败响应
     *
     * @param errorCode 业务错误码
     * @param message   响应消息
     * @param <T>       数据类型
     * @return 响应对象
     */
    public static <T> ReactiveResponse<T> failure(int errorCode, String message) {
        ReactiveResponse<T> response = new ReactiveResponse<>();
        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setData(null);
        response.setSuccess(false);
        return response;
    }

    /**
     * 从异常创建失败响应
     *
     * @param e   业务异常
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> ReactiveResponse<T> failure(BusinessException e) {
        return failure(e.getErrorCode(), e.getMessage());
    }

    /**
     * 从异常创建失败响应
     *
     * @param e   异常
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> ReactiveResponse<T> failure(Throwable e) {
        return failure(ResponseCode.ERROR.getErrorCode(), e.getMessage());
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ReactiveResponse{" +
                "errorCode=" + errorCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", success=" + success +
                ", timestamp=" + timestamp +
                '}';
    }
}