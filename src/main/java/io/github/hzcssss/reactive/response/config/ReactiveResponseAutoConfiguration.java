package io.github.hzcssss.reactive.response.config;

import io.github.hzcssss.reactive.response.service.SpringReactiveResponseBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot自动配置类
 * 用于自动注册SpringReactiveResponseBuilder服务
 */
@Configuration
@AutoConfiguration
@ComponentScan("io.github.hzcssss.reactive.response")
public class ReactiveResponseAutoConfiguration {


    /**
     * 注册SpringReactiveResponseBuilder服务
     * 只有当容器中不存在该类型的Bean时才会创建
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringReactiveResponseBuilder springReactiveResponseBuilder() {
        return new SpringReactiveResponseBuilder();
    }
}
