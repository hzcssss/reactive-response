package io.github.hzcssss.reactive.response.builder;

import io.github.hzcssss.reactive.response.core.ReactiveResponse;
import io.github.hzcssss.reactive.response.exception.BusinessException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

/**
 * 响应式响应构建器
 * 支持链式调用
 */
public class ReactiveResponseBuilder<T> {

    private int errorCode = 0;
    private String message = "操作成功";
    private T data;
    private boolean success = true;

    /**
     * 创建一个成功响应构建器
     *
     * @param <T> 数据类型
     * @return 响应构建器
     */
    public static <T> ReactiveResponseBuilder<T> success() {
        return new ReactiveResponseBuilder<>();
    }

    /**
     * 创建一个成功响应构建器，带数据
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 响应构建器
     */
    public static <T> ReactiveResponseBuilder<T> success(T data) {
        return new ReactiveResponseBuilder<T>().data(data);
    }

    /**
     * 创建一个失败响应构建器
     *
     * @param <T> 数据类型
     * @return 响应构建器
     */
    public static <T> ReactiveResponseBuilder<T> failure() {
        return new ReactiveResponseBuilder<T>()
                .errorCode(1000)
                .message("操作失败")
                .success(false);
    }

    /**
     * 创建一个失败响应构建器，带消息
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 响应构建器
     */
    public static <T> ReactiveResponseBuilder<T> failure(String message) {
        return new ReactiveResponseBuilder<T>()
                .errorCode(1000)
                .message(message)
                .success(false);
    }

    /**
     * 创建一个失败响应构建器，带错误码和消息
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param <T>       数据类型
     * @return 响应构建器
     */
    public static <T> ReactiveResponseBuilder<T> failure(int errorCode, String message) {
        return new ReactiveResponseBuilder<T>()
                .errorCode(errorCode)
                .message(message)
                .success(false);
    }

    /**
     * 从异常创建一个失败响应构建器
     *
     * @param e   业务异常
     * @param <T> 数据类型
     * @return 响应构建器
     */
    public static <T> ReactiveResponseBuilder<T> failure(BusinessException e) {
        return failure(e.getErrorCode(), e.getMessage());
    }

    /**
     * 从异常创建一个失败响应构建器
     *
     * @param e   异常
     * @param <T> 数据类型
     * @return 响应构建器
     */
    public static <T> ReactiveResponseBuilder<T> failure(Throwable e) {
        return failure(9999, e.getMessage());
    }

    /**
     * 从Mono创建响应构建器
     *
     * @param mono 原始Mono
     * @param <T>  数据类型
     * @return 响应构建器
     */
    public static <T> MonoResponseBuilder<T> from(Mono<T> mono) {
        return new MonoResponseBuilder<>(mono);
    }

    /**
     * 从Flux创建响应构建器
     *
     * @param flux 原始Flux
     * @param <T>  数据类型
     * @return 响应构建器
     */
    public static <T> FluxResponseBuilder<T> from(Flux<T> flux) {
        return new FluxResponseBuilder<>(flux);
    }

    /**
     * 设置错误码
     *
     * @param errorCode 错误码
     * @return 响应构建器
     */
    public ReactiveResponseBuilder<T> errorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    /**
     * 设置消息
     *
     * @param message 消息
     * @return 响应构建器
     */
    public ReactiveResponseBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    /**
     * 设置数据
     *
     * @param data 数据
     * @return 响应构建器
     */
    public ReactiveResponseBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    /**
     * 设置成功状态
     *
     * @param success 成功状态
     * @return 响应构建器
     */
    public ReactiveResponseBuilder<T> success(boolean success) {
        this.success = success;
        return this;
    }

    /**
     * 构建响应对象
     *
     * @return 响应对象
     */
    public ReactiveResponse<T> build() {
        ReactiveResponse<T> response = new ReactiveResponse<>();
        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setData(data);
        response.setSuccess(success);
        return response;
    }

    /**
     * 包装Mono
     *
     * @param mono 原始Mono
     * @param <R>  数据类型
     * @return 包装后的Mono
     */
    public static <R> Mono<ReactiveResponse<R>> wrapMono(Mono<R> mono) {
        return mono
                .map(data -> ReactiveResponseBuilder.<R>success(data).build())
                .onErrorResume(BusinessException.class, e -> 
                    Mono.just(ReactiveResponseBuilder.<R>failure(e).build()))
                .onErrorResume(e -> 
                    Mono.just(ReactiveResponseBuilder.<R>failure(e).build()));
    }

    /**
     * 包装Flux
     *
     * @param flux 原始Flux
     * @param <R>  数据类型
     * @return 包装后的Mono
     */
    public static <R> Mono<ReactiveResponse<List<R>>> wrapFlux(Flux<R> flux) {
        return flux
                .collectList()
                .map(data -> ReactiveResponseBuilder.<List<R>>success(data).build())
                .onErrorResume(BusinessException.class, e -> 
                    Mono.just(ReactiveResponseBuilder.<List<R>>failure(e).build()))
                .onErrorResume(e -> 
                    Mono.just(ReactiveResponseBuilder.<List<R>>failure(e).build()));
    }

    /**
     * 解包响应
     *
     * @param responseMono 响应Mono
     * @param <R>          数据类型
     * @return 解包后的Mono
     */
    public static <R> Mono<R> unwrapMono(Mono<ReactiveResponse<R>> responseMono) {
        return responseMono.flatMap(response -> {
            if (response.isSuccess()) {
                return Mono.justOrEmpty(response.getData());
            } else {
                return Mono.error(new BusinessException(response.getErrorCode(), response.getMessage()));
            }
        });
    }

    /**
     * 转换响应数据
     *
     * @param responseMono 响应Mono
     * @param mapper       转换函数
     * @param <R>          原始数据类型
     * @param <U>          目标数据类型
     * @return 转换后的响应Mono
     */
    public static <R, U> Mono<ReactiveResponse<U>> mapResponse(
            Mono<ReactiveResponse<R>> responseMono,
            Function<R, U> mapper) {
        return responseMono.map(response -> {
            if (response.isSuccess() && response.getData() != null) {
                ReactiveResponseBuilder<U> builder = ReactiveResponseBuilder.<U>success()
                        .errorCode(response.getErrorCode())
                        .message(response.getMessage())
                        .data(mapper.apply(response.getData()));
                return builder.build();
            } else {
                ReactiveResponseBuilder<U> builder = ReactiveResponseBuilder.<U>failure()
                        .errorCode(response.getErrorCode())
                        .message(response.getMessage());
                return builder.build();
            }
        });
    }

    /**
     * Mono响应构建器
     * 用于从Mono创建响应
     *
     * @param <T> 数据类型
     */
    public static class MonoResponseBuilder<T> {
        private final Mono<T> mono;
        private int successCode = 0;
        private String successMessage = "操作成功";
        private Function<BusinessException, ReactiveResponse<T>> businessExceptionHandler;
        private Function<Throwable, ReactiveResponse<T>> errorHandler;

        private MonoResponseBuilder(Mono<T> mono) {
            this.mono = mono;
        }

        /**
         * 设置成功响应码
         *
         * @param successCode 成功响应码
         * @return 构建器
         */
        public MonoResponseBuilder<T> successCode(int successCode) {
            this.successCode = successCode;
            return this;
        }

        /**
         * 设置成功消息
         *
         * @param successMessage 成功消息
         * @return 构建器
         */
        public MonoResponseBuilder<T> successMessage(String successMessage) {
            this.successMessage = successMessage;
            return this;
        }

        /**
         * 设置业务异常处理器
         *
         * @param handler 业务异常处理器
         * @return 构建器
         */
        public MonoResponseBuilder<T> onBusinessException(Function<BusinessException, ReactiveResponse<T>> handler) {
            this.businessExceptionHandler = handler;
            return this;
        }

        /**
         * 设置错误处理器
         *
         * @param handler 错误处理器
         * @return 构建器
         */
        public MonoResponseBuilder<T> onError(Function<Throwable, ReactiveResponse<T>> handler) {
            this.errorHandler = handler;
            return this;
        }

        /**
         * 构建响应Mono
         *
         * @return 响应Mono
         */
        public Mono<ReactiveResponse<T>> build() {
            return mono
                    .map(data -> {
                        ReactiveResponse<T> response = new ReactiveResponse<>();
                        response.setErrorCode(successCode);
                        response.setMessage(successMessage);
                        response.setData(data);
                        response.setSuccess(true);
                        return response;
                    })
                    .onErrorResume(BusinessException.class, e -> {
                        if (businessExceptionHandler != null) {
                            return Mono.just(businessExceptionHandler.apply(e));
                        } else {
                            return Mono.just(ReactiveResponseBuilder.<T>failure(e).build());
                        }
                    })
                    .onErrorResume(e -> {
                        if (errorHandler != null) {
                            return Mono.just(errorHandler.apply(e));
                        } else {
                            return Mono.just(ReactiveResponseBuilder.<T>failure(e).build());
                        }
                    });
        }
    }

    /**
     * Flux响应构建器
     * 用于从Flux创建响应
     *
     * @param <T> 数据类型
     */
    public static class FluxResponseBuilder<T> {
        private final Flux<T> flux;
        private int successCode = 0;
        private String successMessage = "操作成功";
        private Function<BusinessException, ReactiveResponse<List<T>>> businessExceptionHandler;
        private Function<Throwable, ReactiveResponse<List<T>>> errorHandler;

        private FluxResponseBuilder(Flux<T> flux) {
            this.flux = flux;
        }

        /**
         * 设置成功响应码
         *
         * @param successCode 成功响应码
         * @return 构建器
         */
        public FluxResponseBuilder<T> successCode(int successCode) {
            this.successCode = successCode;
            return this;
        }

        /**
         * 设置成功消息
         *
         * @param successMessage 成功消息
         * @return 构建器
         */
        public FluxResponseBuilder<T> successMessage(String successMessage) {
            this.successMessage = successMessage;
            return this;
        }

        /**
         * 设置业务异常处理器
         *
         * @param handler 业务异常处理器
         * @return 构建器
         */
        public FluxResponseBuilder<T> onBusinessException(Function<BusinessException, ReactiveResponse<List<T>>> handler) {
            this.businessExceptionHandler = handler;
            return this;
        }

        /**
         * 设置错误处理器
         *
         * @param handler 错误处理器
         * @return 构建器
         */
        public FluxResponseBuilder<T> onError(Function<Throwable, ReactiveResponse<List<T>>> handler) {
            this.errorHandler = handler;
            return this;
        }

        /**
         * 构建响应Mono
         *
         * @return 响应Mono
         */
        public Mono<ReactiveResponse<List<T>>> build() {
            return flux
                    .collectList()
                    .map(data -> {
                        ReactiveResponse<List<T>> response = new ReactiveResponse<>();
                        response.setErrorCode(successCode);
                        response.setMessage(successMessage);
                        response.setData(data);
                        response.setSuccess(true);
                        return response;
                    })
                    .onErrorResume(BusinessException.class, e -> {
                        if (businessExceptionHandler != null) {
                            return Mono.just(businessExceptionHandler.apply(e));
                        } else {
                            return Mono.just(ReactiveResponseBuilder.<List<T>>failure(e).build());
                        }
                    })
                    .onErrorResume(e -> {
                        if (errorHandler != null) {
                            return Mono.just(errorHandler.apply(e));
                        } else {
                            return Mono.just(ReactiveResponseBuilder.<List<T>>failure(e).build());
                        }
                    });
        }
    }
}