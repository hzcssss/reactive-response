package io.github.hzcssss.reactive.response.config;

import io.github.hzcssss.reactive.response.service.SpringReactiveResponseBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot自动配置类
 */
@Configuration
public class ReactiveResponseAutoConfiguration {

    /**
     * 注册SpringReactiveResponseBuilder服务
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringReactiveResponseBuilder springReactiveResponseBuilder() {
        return new SpringReactiveResponseBuilder();
    }
}