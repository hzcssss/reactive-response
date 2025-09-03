# Reactive Response

一个用于响应式编程的API响应包装器，主要用于Spring WebFlux项目。

## 特性

- 统一的API响应格式
- 支持响应式编程（Reactor框架的Mono和Flux）
- 支持Spring Boot自动配置
- 支持链式构建器模式
- 支持业务错误码
- 支持非Spring Boot项目
- 兼容多种Spring Boot版本

## 安装

### Maven

```xml
<dependency>
    <groupId>io.github.hzcssss</groupId>
    <artifactId>reactive-response-builder</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.hzcssss:reactive-response-builder:1.0.0'
```

## 版本兼容性

本库设计为与多种Spring Boot版本兼容：

- Spring Boot 2.x（已测试2.7.x）
- Spring Boot 3.x（理论兼容，需要Java 17+）

所有Spring相关依赖都被标记为`provided`和`optional`，这意味着：

1. 本库不会强制使用特定版本的Spring Boot
2. 本库会使用您项目中已有的Spring Boot版本
3. 如果您的项目不使用Spring Boot，本库仍然可以正常工作

## 使用方法

### Spring Boot项目

在Spring Boot项目中，可以直接注入`SpringReactiveResponseBuilder`：

```java
import io.github.hzcssss.reactive.response.core.ReactiveResponse;
import io.github.hzcssss.reactive.response.service.SpringReactiveResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final SpringReactiveResponseBuilder responseBuilder;
    private final UserService userService;

    @Autowired
    public UserController(SpringReactiveResponseBuilder responseBuilder, UserService userService) {
        this.responseBuilder = responseBuilder;
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public Mono<ReactiveResponse<User>> getUser(@PathVariable String id) {
        return responseBuilder.from(userService.findById(id))
                .successCode(0)
                .successMessage("获取用户成功")
                .build();
    }

    @GetMapping("/users")
    public Mono<ReactiveResponse<List<User>>> getAllUsers() {
        return responseBuilder.from(userService.findAll())
                .successCode(0)
                .successMessage("获取所有用户成功")
                .build();
    }
}
```

### 非Spring Boot项目

在非Spring Boot项目中，可以使用`ReactiveResponseBuilder`或`ReactiveResponseService`：

```java
import io.github.hzcssss.reactive.response.builder.ReactiveResponseBuilder;
import io.github.hzcssss.reactive.response.core.ReactiveResponse;
import io.github.hzcssss.reactive.response.service.ReactiveResponseService;
import reactor.core.publisher.Mono;

// 使用构建器
Mono<User> userMono = findUserById(id);
Mono<ReactiveResponse<User>> response = ReactiveResponseBuilder.wrapMono(userMono);

// 使用服务类
ReactiveResponseService service = new ReactiveResponseService();
Mono<ReactiveResponse<User>> response = service.wrapMono(userMono);
```

### 链式构建器

```java
import io.github.hzcssss.reactive.response.builder.ReactiveResponseBuilder;
import io.github.hzcssss.reactive.response.core.ReactiveResponse;

// 创建成功响应
ReactiveResponse<User> response = ReactiveResponseBuilder.<User>success()
        .errorCode(0)
        .message("操作成功")
        .data(user)
        .build();

// 创建失败响应
ReactiveResponse<User> response = ReactiveResponseBuilder.<User>failure()
        .errorCode(1001)
        .message("用户不存在")
        .build();
```

### 包装Mono和Flux

```java
import io.github.hzcssss.reactive.response.builder.ReactiveResponseBuilder;
import io.github.hzcssss.reactive.response.core.ReactiveResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// 包装Mono
Mono<User> userMono = userService.findById(id);
Mono<ReactiveResponse<User>> response = ReactiveResponseBuilder.wrapMono(userMono);

// 包装Flux
Flux<User> userFlux = userService.findAll();
Mono<ReactiveResponse<List<User>>> response = ReactiveResponseBuilder.wrapFlux(userFlux);
```

### 高级用法

```java
import io.github.hzcssss.reactive.response.builder.ReactiveResponseBuilder;
import io.github.hzcssss.reactive.response.core.ReactiveResponse;
import reactor.core.publisher.Mono;

// 自定义错误处理
return responseBuilder.from(userService.save(user))
        .successCode(0)
        .successMessage("创建用户成功")
        .onBusinessException(e -> responseBuilder.<User>failure()
                .errorCode(e.getErrorCode())
                .message("创建用户失败: " + e.getMessage())
                .build())
        .onError(e -> responseBuilder.<User>failure()
                .errorCode(9999)
                .message("系统错误: " + e.getMessage())
                .build())
        .build();

// 转换响应数据
Mono<ReactiveResponse<User>> userResponse = ReactiveResponseBuilder.wrapMono(findUserById(id));
Mono<ReactiveResponse<UserDTO>> dtoResponse = ReactiveResponseBuilder.mapResponse(
        userResponse, 
        user -> new UserDTO(user.getName(), user.getAge())
);
```

## 响应格式

```json
{
  "success": true,
  "errorCode": 0,
  "message": "操作成功",
  "data": {
    "id": "1",
    "name": "张三",
    "age": 30
  },
  "timestamp": 1631234567890
}
```

## 发布到Maven中央仓库

本项目使用`central-publishing-maven-plugin`发布到Maven中央仓库。以下是发布步骤：

### 前提条件

1. 在Sonatype OSSRH注册账号：https://central.sonatype.org/
2. 创建并验证命名空间（如`io.github.hzcssss`）
3. 配置GPG密钥用于签名

### 配置settings.xml

在Maven的`settings.xml`文件中添加以下配置：

```xml
<servers>
  <server>
    <id>central</id>
    <username>您的Sonatype用户名</username>
    <password>您的Sonatype密码</password>
  </server>
</servers>
```

### 发布命令

执行以下命令发布到Maven中央仓库：

```bash
# 清理、编译、测试、打包、签名并发布
mvn clean deploy
```

### 发布配置说明

项目的`pom.xml`中已配置了以下发布相关插件：

1. `central-publishing-maven-plugin`：处理发布到Maven中央仓库
   - `autoPublish`设置为`true`，自动发布而不需要手动在Sonatype界面操作
   - `waitUntil`设置为`published`，等待发布完成

2. `maven-gpg-plugin`：用于对构件进行GPG签名

3. `maven-source-plugin`和`maven-javadoc-plugin`：生成源码和JavaDoc包

### 验证发布

发布成功后，可以在以下位置查看：

1. Maven中央仓库：https://repo1.maven.org/maven2/io/github/hzcssss/reactive-response-builder/
2. Maven搜索：https://search.maven.org/artifact/io.github.hzcssss/reactive-response-builder

通常，发布到Maven中央仓库后需要几个小时才能在搜索中显示。

## 许可证

MIT