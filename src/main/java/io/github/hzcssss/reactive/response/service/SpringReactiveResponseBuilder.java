package io.github.hzcssss.reactive.response.service;

import io.github.hzcssss.reactive.response.core.ReactiveResponse;
import io.github.hzcssss.reactive.response.exception.BusinessException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

/**
 * Spring Boot集成的响应构建器服务
 * 提供链式调用API，可以通过Spring Boot自动注入使用
 */
public class SpringReactiveResponseBuilder {

    /**
     * 从Mono创建响应构建器
     *
     * @param mono 原始Mono
     * @param <T>  数据类型
     * @return Mono响应构建器
     */
    public <T> MonoResponseBuilder<T> from(Mono<T> mono) {
        return new MonoResponseBuilder<>(mono);
    }

    /**
     * 从Flux创建响应构建器
     *
     * @param flux 原始Flux
     * @param <T>  数据类型
     * @return Flux响应构建器
     */
    public <T> FluxResponseBuilder<T> from(Flux<T> flux) {
        return new FluxResponseBuilder<>(flux);
    }

    /**
     * 创建成功响应构建器
     *
     * @param <T> 数据类型
     * @return 成功响应构建器
     */
    public <T> SuccessResponseBuilder<T> success() {
        return new SuccessResponseBuilder<>();
    }

    /**
     * 创建失败响应构建器
     *
     * @param <T> 数据类型
     * @return 失败响应构建器
     */
    public <T> FailureResponseBuilder<T> failure() {
        return new FailureResponseBuilder<>();
    }

    /**
     * Mono响应构建器
     *
     * @param <T> 数据类型
     */
    public class MonoResponseBuilder<T> {
        private final Mono<T> mono;
        private int successCode = 0;
        private String successMessage = "操作成功";
        private Function<BusinessException, ReactiveResponse<T>> businessExceptionHandler;
        private Function<Throwable, ReactiveResponse<T>> errorHandler;

        private MonoResponseBuilder(Mono<T> mono) {
            this.mono = mono;
        }

        /**
         * 设置成功码
         *
         * @param successCode 成功码
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
         * 构建响应
         *
         * @return 响应Mono
         */
        public Mono<ReactiveResponse<T>> build() {
            return mono.map(data -> {
                ReactiveResponse<T> response = new ReactiveResponse<>();
                response.setSuccess(true);
                response.setErrorCode(successCode);
                response.setMessage(successMessage);
                response.setData(data);
                response.setTimestamp(System.currentTimeMillis());
                return response;
            }).onErrorResume(BusinessException.class, e -> {
                if (businessExceptionHandler != null) {
                    return Mono.just(businessExceptionHandler.apply(e));
                }
                ReactiveResponse<T> response = new ReactiveResponse<>();
                response.setSuccess(false);
                response.setErrorCode(e.getErrorCode());
                response.setMessage(e.getMessage());
                response.setTimestamp(System.currentTimeMillis());
                return Mono.just(response);
            }).onErrorResume(e -> {
                if (errorHandler != null) {
                    return Mono.just(errorHandler.apply(e));
                }
                ReactiveResponse<T> response = new ReactiveResponse<>();
                response.setSuccess(false);
                response.setErrorCode(9999);
                response.setMessage("系统错误: " + e.getMessage());
                response.setTimestamp(System.currentTimeMillis());
                return Mono.just(response);
            });
        }
    }

    /**
     * Flux响应构建器
     *
     * @param <T> 数据类型
     */
    public class FluxResponseBuilder<T> {
        private final Flux<T> flux;
        private int successCode = 0;
        private String successMessage = "操作成功";
        private Function<BusinessException, ReactiveResponse<List<T>>> businessExceptionHandler;
        private Function<Throwable, ReactiveResponse<List<T>>> errorHandler;

        private FluxResponseBuilder(Flux<T> flux) {
            this.flux = flux;
        }

        /**
         * 设置成功码
         *
         * @param successCode 成功码
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
         * 构建响应
         *
         * @return 响应Mono
         */
        public Mono<ReactiveResponse<List<T>>> build() {
            return flux.collectList().map(data -> {
                ReactiveResponse<List<T>> response = new ReactiveResponse<>();
                response.setSuccess(true);
                response.setErrorCode(successCode);
                response.setMessage(successMessage);
                response.setData(data);
                response.setTimestamp(System.currentTimeMillis());
                return response;
            }).onErrorResume(BusinessException.class, e -> {
                if (businessExceptionHandler != null) {
                    return Mono.just(businessExceptionHandler.apply(e));
                }
                ReactiveResponse<List<T>> response = new ReactiveResponse<>();
                response.setSuccess(false);
                response.setErrorCode(e.getErrorCode());
                response.setMessage(e.getMessage());
                response.setTimestamp(System.currentTimeMillis());
                return Mono.just(response);
            }).onErrorResume(e -> {
                if (errorHandler != null) {
                    return Mono.just(errorHandler.apply(e));
                }
                ReactiveResponse<List<T>> response = new ReactiveResponse<>();
                response.setSuccess(false);
                response.setErrorCode(9999);
                response.setMessage("系统错误: " + e.getMessage());
                response.setTimestamp(System.currentTimeMillis());
                return Mono.just(response);
            });
        }
    }

    /**
     * 成功响应构建器
     *
     * @param <T> 数据类型
     */
    public class SuccessResponseBuilder<T> {
        private int errorCode = 0;
        private String message = "操作成功";
        private T data;

        /**
         * 设置错误码
         *
         * @param errorCode 错误码
         * @return 构建器
         */
        public SuccessResponseBuilder<T> errorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        /**
         * 设置消息
         *
         * @param message 消息
         * @return 构建器
         */
        public SuccessResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        /**
         * 设置数据
         *
         * @param data 数据
         * @return 构建器
         */
        public SuccessResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        /**
         * 构建响应
         *
         * @return 响应
         */
        public ReactiveResponse<T> build() {
            ReactiveResponse<T> response = new ReactiveResponse<>();
            response.setSuccess(true);
            response.setErrorCode(errorCode);
            response.setMessage(message);
            response.setData(data);
            response.setTimestamp(System.currentTimeMillis());
            return response;
        }
    }

    /**
     * 失败响应构建器
     *
     * @param <T> 数据类型
     */
    public class FailureResponseBuilder<T> {
        private int errorCode = 1000;
        private String message = "操作失败";

        /**
         * 设置错误码
         *
         * @param errorCode 错误码
         * @return 构建器
         */
        public FailureResponseBuilder<T> errorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        /**
         * 设置消息
         *
         * @param message 消息
         * @return 构建器
         */
        public FailureResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        /**
         * 构建响应
         *
         * @return 响应
         */
        public ReactiveResponse<T> build() {
            ReactiveResponse<T> response = new ReactiveResponse<>();
            response.setSuccess(false);
            response.setErrorCode(errorCode);
            response.setMessage(message);
            response.setTimestamp(System.currentTimeMillis());
            return response;
        }
    }
}