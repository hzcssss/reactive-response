# API参考

本文档提供 `reactive-response` 库的完整API参考，包括基础构建器、Spring集成构建器和工具类的详细用法。

## 基础构建器API

### ReactiveResponseBuilder

`ReactiveResponseBuilder` 是核心的响应构建器，提供静态工厂方法和包装方法来创建标准化的响应对象。

#### 静态工厂方法

##### 成功响应构建器

```java
// 创建空的成功响应构建器
public static <T> SuccessResponseBuilder<T> success()

// 创建包含数据的成功响应构建器
public static <T> SuccessResponseBuilder<T> success(T data)

// 创建包含数据和消息的成功响应构建器
public static <T> SuccessResponseBuilder<T> success(T data, String message)
```

**使用示例：**

```java
// 基本成功响应
ReactiveResponse<String> response = ReactiveResponseBuilder.<String>success()
        .data("操作完成")
        .message("执行成功")
        .build();

// 带数据的成功响应
ReactiveResponse<User> userResponse = ReactiveResponseBuilder.success(user)
        .message("用户创建成功")
        .build();

// 带数据和消息的成功响应
ReactiveResponse<User> response = ReactiveResponseBuilder.success(user, "查询成功")
        .errorCode(200)
        .build();
```

##### 失败响应构建器

```java
// 创建默认失败响应构建器（错误码1000，消息"操作失败"）
public static <T> FailureResponseBuilder<T> failure()

// 创建包含自定义消息的失败响应构建器
public static <T> FailureResponseBuilder<T> failure(String message)

// 创建包含自定义错误码和消息的失败响应构建器
public static <T> FailureResponseBuilder<T> failure(int errorCode, String message)

// 从业务异常创建失败响应构建器
public static <T> FailureResponseBuilder<T> failure(BusinessException e)

// 从任意异常创建失败响应构建器
public static <T> FailureResponseBuilder<T> failure(Throwable e)
```

**使用示例：**

```java
// 默认失败响应
ReactiveResponse<Object> response = ReactiveResponseBuilder.failure()
        .build();

// 自定义消息失败响应
ReactiveResponse<Object> response = ReactiveResponseBuilder.failure("用户不存在")
        .errorCode(1004)
        .build();

// 从异常创建失败响应
ReactiveResponse<Object> response = ReactiveResponseBuilder.failure(businessException)
        .build();
```

#### 包装方法

##### wrapMono 方法

将 `Mono<T>` 包装为 `Mono<ReactiveResponse<T>>`，自动处理成功和异常情况。

```java
public static <T> Mono<ReactiveResponse<T>> wrapMono(Mono<T> mono)
```

**行为说明：**
- 当 `Mono` 正常发出数据时，创建成功响应
- 当发生 `BusinessException` 时，创建对应的失败响应
- 对于其他异常，创建系统错误响应（错误码9999）

**使用示例：**

```java
// 包装用户查询Mono
Mono<User> userMono = userService.findById(1L);
Mono<ReactiveResponse<User>> response = ReactiveResponseBuilder.wrapMono(userMono);

// 自动处理异常
Mono<User> userMono = userService.findById(999L)  // 可能抛出BusinessException
Mono<ReactiveResponse<User>> response = ReactiveResponseBuilder.wrapMono(userMono);
```

##### wrapFlux 方法

将 `Flux<T>` 包装为 `Mono<ReactiveResponse<List<T>>>`，将流中的所有元素收集为列表。

```java
public static <T> Mono<ReactiveResponse<List<T>>> wrapFlux(Flux<T> flux)
```

**使用示例：**

```java
// 包装用户列表Flux
Flux<User> userFlux = userService.findAllActiveUsers();
Mono<ReactiveResponse<List<User>>> response = ReactiveResponseBuilder.wrapFlux(userFlux);
```

##### unwrapMono 方法

解包 `Mono<ReactiveResponse<T>>`，如果响应成功则返回数据，否则抛出异常。

```java
public static <T> Mono<T> unwrapMono(Mono<ReactiveResponse<T>> responseMono)
```

**使用示例：**

```java
// 解包响应数据
Mono<ReactiveResponse<User>> responseMono = getUserResponse();
Mono<User> userMono = ReactiveResponseBuilder.unwrapMono(responseMono);
```

#### 嵌套构建器类

##### SuccessResponseBuilder

用于构建成功响应，支持链式调用配置。

```java
public static class SuccessResponseBuilder<T> {
    public SuccessResponseBuilder<T> errorCode(int errorCode)
    public SuccessResponseBuilder<T> message(String message)
    public SuccessResponseBuilder<T> data(T data)
    public ReactiveResponse<T> build()
}
```

##### FailureResponseBuilder

用于构建失败响应，支持链式调用配置。

```java
public static class FailureResponseBuilder<T> {
    public FailureResponseBuilder<T> errorCode(int errorCode)
    public FailureResponseBuilder<T> message(String message)
    public ReactiveResponse<T> build()
}
```

#### 响应式流构建方法

```java
// 从Mono创建响应构建器
public static <T> MonoFromBuilder<T> from(Mono<T> mono)

// 从Flux创建响应构建器
public static <T> FluxFromBuilder<T> from(Flux<T> flux)
```

这些方法返回专门的构建器，支持更高级的配置：

```java
// 从Mono构建带异常处理的响应
Mono<ReactiveResponse<User>> response = ReactiveResponseBuilder.from(userMono)
        .successMessage("查询成功")
        .onBusinessException(ex -> ResponseCode.NOT_FOUND)
        .onError(ex -> ResponseCode.ERROR)
        .build();
```

## Spring集成构建器API

### SpringReactiveResponseBuilder

`SpringReactiveResponseBuilder` 是专为Spring框架设计的响应构建器，通过依赖注入机制提供更便捷的使用方式。

#### 依赖注入

```java
@RestController
public class UserController {
    
    private final SpringReactiveResponseBuilder responseBuilder;
    
    // 构造函数注入
    public UserController(SpringReactiveResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }
    
    // 或者字段注入
    @Autowired
    private SpringReactiveResponseBuilder responseBuilder;
}
```

#### 响应式流构建

##### from(Mono<T>) 方法

```java
public <T> MonoResponseBuilder<T> from(Mono<T> mono)
```

返回 `MonoResponseBuilder` 实例，支持链式配置：

```java
@GetMapping("/users/{id}")
public Mono<ReactiveResponse<User>> getUser(@PathVariable Long id) {
    return responseBuilder.from(userService.findById(id))
            .successMessage("用户查询成功")
            .onBusinessException(this::handleBusinessException)
            .onError(this::handleSystemError)
            .build();
}

private ResponseCode handleBusinessException(BusinessException ex) {
    return switch (ex.getErrorCode()) {
        case 1004 -> ResponseCode.NOT_FOUND;
        case 1001 -> ResponseCode.UNAUTHORIZED;
        default -> ResponseCode.ERROR;
    };
}

private ResponseCode handleSystemError(Throwable ex) {
    return ResponseCode.ERROR;
}
```

##### from(Flux<T>) 方法

```java
public <T> FluxResponseBuilder<T> from(Flux<T> flux)
```

返回 `FluxResponseBuilder` 实例：

```java
@GetMapping("/users")
public Mono<ReactiveResponse<List<User>>> getAllUsers() {
    return responseBuilder.from(userService.findAll())
            .successMessage("用户列表查询成功")
            .build();
}
```

#### 直接响应构建

##### success() 方法

```java
public <T> SuccessResponseBuilder<T> success()
public <T> SuccessResponseBuilder<T> success(T data)
```

**使用示例：**

```java
@PostMapping("/users")
public Mono<ReactiveResponse<User>> createUser(@RequestBody CreateUserRequest request) {
    return userService.createUser(request)
            .map(user -> responseBuilder.success(user)
                    .message("用户创建成功")
                    .errorCode(201)
                    .build());
}
```

##### failure() 方法

```java
public <T> FailureResponseBuilder<T> failure()
public <T> FailureResponseBuilder<T> failure(String message)
public <T> FailureResponseBuilder<T> failure(int errorCode, String message)
```

**使用示例：**

```java
@DeleteMapping("/users/{id}")
public Mono<ReactiveResponse<Object>> deleteUser(@PathVariable Long id) {
    return userService.deleteById(id)
            .then(Mono.fromSupplier(() -> responseBuilder.success()
                    .message("用户删除成功")
                    .build()))
            .onErrorReturn(responseBuilder.failure("删除失败")
                    .errorCode(500)
                    .build());
}
```

#### 嵌套构建器类

##### MonoResponseBuilder

用于从 `Mono<T>` 构建响应的链式构建器：

```java
public class MonoResponseBuilder<T> {
    public MonoResponseBuilder<T> successCode(int successCode)
    public MonoResponseBuilder<T> successMessage(String successMessage)
    public MonoResponseBuilder<T> onBusinessException(Function<BusinessException, ResponseCode> handler)
    public MonoResponseBuilder<T> onError(Function<Throwable, ResponseCode> handler)
    public Mono<ReactiveResponse<T>> build()
}
```

##### FluxResponseBuilder

用于从 `Flux<T>` 构建响应的链式构建器：

```java
public class FluxResponseBuilder<T> {
    public FluxResponseBuilder<T> successCode(int successCode)
    public FluxResponseBuilder<T> successMessage(String successMessage)
    public FluxResponseBuilder<T> onBusinessException(Function<BusinessException, ResponseCode> handler)
    public FluxResponseBuilder<T> onError(Function<Throwable, ResponseCode> handler)
    public Mono<ReactiveResponse<List<T>>> build()
}
```

##### 完整使用示例

```java
@Service
public class UserService {
    
    private final SpringReactiveResponseBuilder responseBuilder;
    private final UserRepository userRepository;
    
    public Mono<ReactiveResponse<User>> getUserWithFullHandling(Long id) {
        return responseBuilder.from(findUserById(id))
                .successCode(200)
                .successMessage("用户查询成功")
                .onBusinessException(ex -> {
                    log.warn("业务异常: {}", ex.getMessage());
                    return ResponseCode.NOT_FOUND;
                })
                .onError(ex -> {
                    log.error("系统异常", ex);
                    return ResponseCode.ERROR;
                })
                .build();
    }
    
    private Mono<User> findUserById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(
                    ResponseCode.NOT_FOUND, "用户ID:" + id + "不存在")));
    }
}
```

## 工具类API

### ReactiveResponseUtil

`ReactiveResponseUtil` 提供了一组静态工具方法，用于简化响应处理。

#### 核心方法

##### wrapMono 方法

```java
public static <T> Mono<ReactiveResponse<T>> wrapMono(Mono<T> mono)
```

功能与 `ReactiveResponseBuilder.wrapMono()` 相同，但实现更简洁。

##### wrapFlux 方法

```java
public static <T> Mono<ReactiveResponse<List<T>>> wrapFlux(Flux<T> flux)
```

功能与 `ReactiveResponseBuilder.wrapFlux()` 相同。

##### unwrapMono 方法

```java
public static <T> Mono<T> unwrapMono(Mono<ReactiveResponse<T>> responseMono)
```

解包响应，失败时抛出异常。

#### 便捷方法

```java
// 创建成功响应
public static <T> Mono<ReactiveResponse<T>> success(T data)
public static <T> Mono<ReactiveResponse<T>> success(T data, String message)

// 创建失败响应
public static <T> Mono<ReactiveResponse<T>> error(String message)
public static <T> Mono<ReactiveResponse<T>> error(String message, int errorCode)
```

**使用示例：**

```java
import static io.github.hzcssss.reactive.response.util.ReactiveResponseUtil.*;

@RestController
public class QuickController {
    
    @GetMapping("/quick-success")
    public Mono<ReactiveResponse<String>> quickSuccess() {
        return success("快速成功响应");
    }
    
    @GetMapping("/quick-error")
    public Mono<ReactiveResponse<Object>> quickError() {
        return error("快速错误响应", 400);
    }
    
    @GetMapping("/wrap-mono")
    public Mono<ReactiveResponse<User>> wrapUserMono() {
        Mono<User> userMono = userService.getCurrentUser();
        return wrapMono(userMono);
    }
}
```

## 核心响应类与异常

### ReactiveResponse

通用响应包装类，包含以下字段：

```java
public class ReactiveResponse<T> {
    private int errorCode;      // 错误码
    private String message;     // 响应消息
    private T data;            // 响应数据
    private boolean success;   // 成功状态
    private long timestamp;    // 时间戳
    
    // 静态工厂方法
    public static <T> ReactiveResponse<T> success(T data)
    public static <T> ReactiveResponse<T> success(T data, String message)
    public static <T> ReactiveResponse<T> failure(String message)
    public static <T> ReactiveResponse<T> failure(String message, int errorCode)
    public static <T> ReactiveResponse<T> failure(BusinessException e)
    public static <T> ReactiveResponse<T> failure(Throwable e)
}
```

### ResponseCode

响应码枚举，定义标准的错误码和消息：

```java
public enum ResponseCode {
    SUCCESS(0, "操作成功"),
    UNAUTHORIZED(1001, "认证失败"),
    FORBIDDEN(1002, "权限不足"),
    PARAMETER_ERROR(1003, "参数错误"),
    NOT_FOUND(1004, "资源不存在"),
    ERROR(9999, "服务器错误");
    
    private final int errorCode;
    private final String message;
}
```

### BusinessException

业务异常类，携带错误码和消息：

```java
public class BusinessException extends RuntimeException {
    private int errorCode;
    
    // 构造函数
    public BusinessException(String message)
    public BusinessException(int errorCode, String message)
    public BusinessException(ResponseCode responseCode)
    public BusinessException(String message, Throwable cause)
    public BusinessException(ResponseCode responseCode, Throwable cause)
    
    // getter方法
    public int getErrorCode()
    public String getMessage()
}
```

## 最佳实践

### 1. 选择合适的构建器

- **Spring环境**：使用 `SpringReactiveResponseBuilder`，享受依赖注入的便利
- **非Spring环境**：使用 `ReactiveResponseBuilder` 静态方法
- **简单场景**：使用 `ReactiveResponseUtil` 静态工具方法

### 2. 异常处理配置

```java
// 推荐的异常处理配置
return responseBuilder.from(businessLogic())
        .successMessage("操作成功")
        .onBusinessException(ex -> mapBusinessException(ex))
        .onError(ex -> {
            log.error("系统异常", ex);
            return ResponseCode.ERROR;
        })
        .build();
```

### 3. 链式调用最佳实践

```java
// 清晰的链式调用
return responseBuilder.from(userService.createUser(request))
        .successCode(201)  // HTTP 201 Created
        .successMessage("用户创建成功")
        .onBusinessException(this::handleUserCreationException)
        .build();
```

---

*更多高级用法和性能优化技巧，请参阅 [高级用法](./高级用法.md)。*