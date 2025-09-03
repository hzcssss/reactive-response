# 更新日志

本文档记录了 Reactive Response 库的所有重要变更。

## [1.0.2] - 2025-09-03

### 修复
- 修复了 Spring Boot 自动配置类的问题，确保在所有环境下都能正确注册 `SpringReactiveResponseBuilder` bean
- 添加了 `@ComponentScan` 注解，确保扫描到库中的所有组件
- 移除了条件限制，使自动配置在所有环境下都能生效

### 改进
- 添加了日志记录，帮助调试自动配置过程
- 支持 Spring Boot 2.7+ 的新自动配置机制，添加了 `org.springframework.boot.autoconfigure.AutoConfiguration.imports` 文件

## [1.0.1] - 2025-08-30

### 修复
- 修复了 Spring Boot 自动注入问题，解决了 "Could not autowire. No beans of 'SpringReactiveResponseBuilder' type found" 错误
- 更新了 `ReactiveResponseAutoConfiguration` 类，添加了 `@AutoConfiguration` 注解

### 改进
- 改进了资源打包配置，确保所有资源文件都被正确打包到最终的 JAR 中
- 更新了文档，提供了更详细的使用说明

## [1.0.0] - 2025-08-15

### 新功能
- 初始版本发布
- 提供响应式编程场景下的 API 响应包装功能
- 支持 Mono 和 Flux 的响应包装
- 提供链式调用 API
- 集成 Spring Boot 自动配置