package io.github.hzcssss.reactive.response;

import io.github.hzcssss.reactive.response.config.ReactiveResponseAutoConfiguration;
import io.github.hzcssss.reactive.response.core.ReactiveResponse;
import io.github.hzcssss.reactive.response.exception.BusinessException;
import io.github.hzcssss.reactive.response.service.SpringReactiveResponseBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring Boot集成测试类
 */
@SpringBootTest
@Import(ReactiveResponseAutoConfiguration.class)
public class SpringReactiveResponseTest {

    @Autowired
    private SpringReactiveResponseBuilder responseBuilder;

    @Test
    public void testAutoConfiguration() {
        // 验证自动配置是否正常工作
        assertNotNull(responseBuilder);
    }

    @Test
    public void testMonoResponse() {
        // 创建Mono
        Mono<String> mono = Mono.just("测试数据");
        
        // 使用Spring构建器包装Mono
        Mono<ReactiveResponse<String>> responseMono = responseBuilder.from(mono)
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
    public void testFluxResponse() {
        // 创建Flux
        Flux<String> flux = Flux.fromIterable(Arrays.asList("数据1", "数据2", "数据3"));
        
        // 使用Spring构建器包装Flux
        Mono<ReactiveResponse<List<String>>> responseMono = responseBuilder.from(flux)
                .successCode(0)
                .successMessage("自定义成功消息")
                .build();
        
        // 验证响应
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertTrue(response.isSuccess());
                    assertEquals(0, response.getErrorCode());
                    assertEquals("自定义成功消息", response.getMessage());
                    assertEquals(3, response.getData().size());
                    assertEquals("数据1", response.getData().get(0));
                    assertEquals("数据2", response.getData().get(1));
                    assertEquals("数据3", response.getData().get(2));
                })
                .verifyComplete();
    }

    @Test
    public void testCustomErrorHandling() {
        // 创建会抛出异常的Mono
        Mono<String> mono = Mono.error(new BusinessException(1003, "测试业务异常"));
        
        // 使用Spring构建器包装Mono，并自定义错误处理
        Mono<ReactiveResponse<String>> responseMono = responseBuilder.from(mono)
                .successCode(0)
                .successMessage("成功消息")
                .onBusinessException(e -> responseBuilder.<String>failure()
                        .errorCode(e.getErrorCode())
                        .message("自定义错误处理: " + e.getMessage())
                        .build())
                .build();
        
        // 验证响应
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertFalse(response.isSuccess());
                    assertEquals(1003, response.getErrorCode());
                    assertEquals("自定义错误处理: 测试业务异常", response.getMessage());
                    assertNull(response.getData());
                })
                .verifyComplete();
    }

    @Test
    public void testSuccessBuilder() {
        // 使用Spring构建器创建成功响应
        ReactiveResponse<String> response = responseBuilder.<String>success()
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
    public void testFailureBuilder() {
        // 使用Spring构建器创建失败响应
        ReactiveResponse<String> response = responseBuilder.<String>failure()
                .errorCode(1004)
                .message("自定义失败消息")
                .build();
        
        // 验证响应
        assertFalse(response.isSuccess());
        assertEquals(1004, response.getErrorCode());
        assertEquals("自定义失败消息", response.getMessage());
        assertNull(response.getData());
    }
}