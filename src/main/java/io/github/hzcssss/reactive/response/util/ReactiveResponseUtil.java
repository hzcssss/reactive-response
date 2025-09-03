package io.github.hzcssss.reactive.response.util;

import io.github.hzcssss.reactive.response.core.ReactiveResponse;
import io.github.hzcssss.reactive.response.exception.BusinessException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 响应式响应工具类
 */
public class ReactiveResponseUtil {

    private ReactiveResponseUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 将Mono包装为ReactiveResponse
     *
     * @param mono 原始Mono
     * @param <T>  数据类型
     * @return 包装后的Mono
     */
    public static <T> Mono<ReactiveResponse<T>> wrapMono(Mono<T> mono) {
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
    public static <T> Mono<ReactiveResponse<List<T>>> wrapFlux(Flux<T> flux) {
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
    public static <T> Mono<T> unwrapMono(Mono<ReactiveResponse<T>> responseMono) {
        return responseMono.flatMap(response -> {
            if (response.isSuccess()) {
                return Mono.justOrEmpty(response.getData());
            } else {
                return Mono.error(new BusinessException(response.getErrorCode(), response.getMessage()));
            }
        });
    }
}