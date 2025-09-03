package io.github.hzcssss.reactive.response;

import io.github.hzcssss.reactive.response.builder.ReactiveResponseBuilder;
import io.github.hzcssss.reactive.response.core.ReactiveResponse;
import io.github.hzcssss.reactive.response.exception.BusinessException;
import io.github.hzcssss.reactive.response.util.ReactiveResponseUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 响应式响应测试类
 */
public class ReactiveResponseTest {

    @Test
    public void testSuccessResponse() {
        // 创建成功响应
        ReactiveResponse<String> response = ReactiveResponse.success("测试数据");
        
        // 验证响应
        assertTrue(response.isSuccess());
        assertEquals(0, response.getErrorCode());
        assertEquals("操作成功", response.getMessage());
        assertEquals("测试数据", response.getData());
    }

    @Test
    public void testFailureResponse() {
        // 创建失败响应
        ReactiveResponse<String> response = ReactiveResponse.failure(1001, "操作失败");
        
        // 验证响应
        assertFalse(response.isSuccess());
        assertEquals(1001, response.getErrorCode());
        assertEquals("操作失败", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    public void testBusinessExceptionResponse() {
        // 创建业务异常
        BusinessException exception = new BusinessException(1002, "业务异常");
        
        // 从异常创建响应
        ReactiveResponse<String> response = ReactiveResponse.failure(exception);
        
        // 验证响应
        assertFalse(response.isSuccess());
        assertEquals(1002, response.getErrorCode());
        assertEquals("业务异常", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    public void testWrapMono() {
        // 创建Mono
        Mono<String> mono = Mono.just("测试数据");
        
        // 包装Mono
        Mono<ReactiveResponse<String>> responseMono = ReactiveResponseUtil.wrapMono(mono);
        
        // 验证响应
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertTrue(response.isSuccess());
                    assertEquals(0, response.getErrorCode());
                    assertEquals("操作成功", response.getMessage());
                    assertEquals("测试数据", response.getData());
                })
                .verifyComplete();
    }

    @Test
    public void testWrapFlux() {
        // 创建Flux
        Flux<String> flux = Flux.fromIterable(Arrays.asList("数据1", "数据2", "数据3"));
        
        // 包装Flux
        Mono<ReactiveResponse<List<String>>> responseMono = ReactiveResponseUtil.wrapFlux(flux);
        
        // 验证响应
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertTrue(response.isSuccess());
                    assertEquals(0, response.getErrorCode());
                    assertEquals("操作成功", response.getMessage());
                    assertEquals(3, response.getData().size());
                    assertEquals("数据1", response.getData().get(0));
                    assertEquals("数据2", response.getData().get(1));
                    assertEquals("数据3", response.getData().get(2));
                })
                .verifyComplete();
    }

    @Test
    public void testBuilderPattern() {
        // 使用构建器模式创建响应
        ReactiveResponse<String> response = ReactiveResponseBuilder.<String>success()
                .errorCode(0)
                .message("自定义成功消息")
                .data("测试数据")
                .build();
        
        // 验证响应
        assertTrue(response.isSuccess());
        assertEquals(0, response.getErrorCode());
        assertEquals("自定义成功消息", response.getMessage());
        assertEquals("测试数据", response.getData());
    }

    @Test
    public void testMonoBuilder() {
        // 创建Mono
        Mono<String> mono = Mono.just("测试数据");
        
        // 使用构建器包装Mono
        Mono<ReactiveResponse<String>> responseMono = ReactiveResponseBuilder.from(mono)
                .successCode(0)
                .successMessage("自定义成功消息")
                .build();
        
        // 验证响应
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertTrue(response.isSuccess());
                    assertEquals(0, response.getErrorCode());
                    assertEquals("自定义成功消息", response.getMessage());
                    assertEquals("测试数据", response.getData());
                })
                .verifyComplete();
    }

    @Test
    public void testErrorHandling() {
        // 创建会抛出异常的Mono
        Mono<String> mono = Mono.error(new BusinessException(1003, "测试业务异常"));
        
        // 使用构建器包装Mono
        Mono<ReactiveResponse<String>> responseMono = ReactiveResponseBuilder.from(mono)
                .successCode(0)
                .successMessage("成功消息")
                .build();
        
        // 验证响应
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertFalse(response.isSuccess());
                    assertEquals(1003, response.getErrorCode());
                    assertEquals("测试业务异常", response.getMessage());
                    assertNull(response.getData());
                })
                .verifyComplete();
    }
}