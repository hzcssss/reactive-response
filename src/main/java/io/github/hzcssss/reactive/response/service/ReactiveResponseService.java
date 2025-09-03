package io.github.hzcssss.reactive.response.service;

import io.github.hzcssss.reactive.response.core.ReactiveResponse;
import io.github.hzcssss.reactive.response.exception.BusinessException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 响应式响应服务类
 * 提供链式调用API
 */
public class ReactiveResponseService {

    /**
     * 将Mono包装为ReactiveResponse
     *
     * @param mono 原始Mono
     * @param <T>  数据类型
     * @return 包装后的Mono
     */
    public <T> Mono<ReactiveResponse<T>> wrapMono(Mono<T> mono) {
        return mono
                .map(ReactiveResponse::success)
                .onErrorResume(BusinessException.class, e -> Mono.just(ReactiveResponse.failure(e)))
                .onErrorResume(e -> Mono.just(ReactiveResponse.failure(e)));
    }

    /**
     * 将Flux包装为ReactiveResponse
     *
     * @param flux 原始Flux
     * @param <T>  数据类型
     * @return 包装后的Mono
     */
    public <T> Mono<ReactiveResponse<List<T>>> wrapFlux(Flux<T> flux) {
        return flux
                .collectList()
                .map(ReactiveResponse::success)
                .onErrorResume(BusinessException.class, e -> Mono.just(ReactiveResponse.failure(e)))
                .onErrorResume(e -> Mono.just(ReactiveResponse.failure(e)));
    }

    /**
     * 解包ReactiveResponse，如果失败则抛出异常
     *
     * @param responseMono 响应Mono
     * @param <T>          数据类型
     * @return 解包后的Mono
     */
    public <T> Mono<T> unwrapMono(Mono<ReactiveResponse<T>> responseMono) {
        return responseMono.flatMap(response -> {
            if (response.isSuccess()) {
                return Mono.justOrEmpty(response.getData());
            } else {
                return Mono.error(new BusinessException(response.getErrorCode(), response.getMessage()));
            }
        });
    }

    /**
     * 创建成功响应
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public <T> ReactiveResponse<T> success() {
        return ReactiveResponse.success();
    }

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 响应对象
     */
    public <T> ReactiveResponse<T> success(T data) {
        return ReactiveResponse.success(data);
    }

    /**
     * 创建成功响应
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 响应对象
     */
    public <T> ReactiveResponse<T> success(String message, T data) {
        return ReactiveResponse.success(message, data);
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
    public <T> ReactiveResponse<T> success(int errorCode, String message, T data) {
        return ReactiveResponse.success(errorCode, message, data);
    }

    /**
     * 创建失败响应
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public <T> ReactiveResponse<T> failure() {
        return ReactiveResponse.failure();
    }

    /**
     * 创建失败响应
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public <T> ReactiveResponse<T> failure(String message) {
        return ReactiveResponse.failure(message);
    }

    /**
     * 创建失败响应
     *
     * @param errorCode 业务错误码
     * @param message   响应消息
     * @param <T>       数据类型
     * @return 响应对象
     */
    public <T> ReactiveResponse<T> failure(int errorCode, String message) {
        return ReactiveResponse.failure(errorCode, message);
    }

    /**
     * 从异常创建失败响应
     *
     * @param e   业务异常
     * @param <T> 数据类型
     * @return 响应对象
     */
    public <T> ReactiveResponse<T> failure(BusinessException e) {
        return ReactiveResponse.failure(e);
    }

    /**
     * 从异常创建失败响应
     *
     * @param e   异常
     * @param <T> 数据类型
     * @return 响应对象
     */
    public <T> ReactiveResponse<T> failure(Throwable e) {
        return ReactiveResponse.failure(e);
    }
}