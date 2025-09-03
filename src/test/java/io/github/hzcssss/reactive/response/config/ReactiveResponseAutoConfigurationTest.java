package io.github.hzcssss.reactive.response.config;

import io.github.hzcssss.reactive.response.service.SpringReactiveResponseBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 自动配置测试类
 */
public class ReactiveResponseAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ReactiveResponseAutoConfiguration.class));

    @Test
    public void testAutoConfiguration() {
        // 测试自动配置是否正常工作
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SpringReactiveResponseBuilder.class);
        });
    }

    @Test
    public void testCustomConfiguration() {
        // 测试自定义配置是否会覆盖自动配置
        contextRunner
                .withUserConfiguration(CustomConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(SpringReactiveResponseBuilder.class);
                    assertThat(context.getBean(SpringReactiveResponseBuilder.class))
                            .isInstanceOf(CustomSpringReactiveResponseBuilder.class);
                });
    }

    // 自定义配置类
    static class CustomConfiguration {
        @org.springframework.context.annotation.Bean
        public SpringReactiveResponseBuilder springReactiveResponseBuilder() {
            return new CustomSpringReactiveResponseBuilder();
        }
    }

    // 自定义响应构建器
    static class CustomSpringReactiveResponseBuilder extends SpringReactiveResponseBuilder {
        // 自定义实现
    }
}