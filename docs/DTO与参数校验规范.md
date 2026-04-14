## 请求/响应 DTO、分页封装、参数校验

### 一、请求 DTO（Request DTO）

#### 1. 基础请求 DTO

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/dto/request/BaseRequest.java**

```java
package com.ddd.platform.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "基础请求参数")
public class BaseRequest implements Serializable {
    
    @Schema(description = "请求ID", example = "req_123456")
    private String requestId;
    
    @Schema(description = "客户端版本", example = "1.0.0")
    private String version = "1.0.0";
    
    @Schema(description = "时间戳", example = "1702364207900")
    private Long timestamp = System.currentTimeMillis();
}
```

#### 2. 用户注册请求 DTO

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/dto/request/UserRegisterRequest.java**

```java
package com.ddd.platform.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户注册请求")
public class UserRegisterRequest extends BaseRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Schema(description = "用户名", example = "zhangsan", required = true)
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    @Schema(description = "密码", example = "123456", required = true)
    private String password;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "zhangsan@example.com", required = true)
    private String email;
    
    @Size(max = 50, message = "昵称长度不能超过50")
    @Schema(description = "昵称", example = "张三")
    private String nickname;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    @Schema(description = "验证码", example = "123456")
    private String verifyCode;
}
```

#### 3. 用户登录请求 DTO

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/dto/request/UserLoginRequest.java**

```java
package com.ddd.platform.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户登录请求")
public class UserLoginRequest extends BaseRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "admin", required = true)
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456", required = true)
    private String password;
    
    @Schema(description = "记住我", example = "true")
    private Boolean rememberMe = false;
    
    @Schema(description = "验证码", example = "1234")
    private String captcha;
}
```

#### 4. 用户更新请求 DTO

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/dto/request/UserUpdateRequest.java**

```java
package com.ddd.platform.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户更新请求")
public class UserUpdateRequest extends BaseRequest {
    
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "newemail@example.com")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13900139000")
    private String phone;
    
    @Size(max = 50, message = "昵称长度不能超过50")
    @Schema(description = "昵称", example = "新昵称")
    private String nickname;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @Size(max = 200, message = "个人简介长度不能超过200")
    @Schema(description = "个人简介", example = "这是我的个人简介")
    private String bio;
}
```

#### 5. 分页请求 DTO

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/dto/request/PageRequest.java**

```java
package com.ddd.platform.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "分页请求")
public class PageRequest extends BaseRequest {
    
    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", example = "1", defaultValue = "1")
    private Integer pageNum = 1;
    
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页条数最大为100")
    @Schema(description = "每页条数", example = "10", defaultValue = "10")
    private Integer pageSize = 10;
    
    @Schema(description = "排序字段", example = "createTime")
    private String orderBy;
    
    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String orderDirection = "DESC";
}
```

#### 6. 用户分页查询请求

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/dto/request/UserPageRequest.java**

```java
package com.ddd.platform.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户分页查询请求")
public class UserPageRequest extends PageRequest {
    
    @Schema(description = "用户名关键词", example = "admin")
    private String username;
    
    @Schema(description = "邮箱关键词", example = "@example.com")
    private String email;
    
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;
    
    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    private String startTime;
    
    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    private String endTime;
}
```

### 二、响应 DTO（Response DTO）

#### 1. 用户响应 DTO

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/dto/response/UserResponse.java**

```java
package com.ddd.platform.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "用户响应")
public class UserResponse {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;
    
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    @Schema(description = "昵称", example = "管理员")
    private String nickname;
    
    @Schema(description = "头像", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @Schema(description = "个人简介", example = "系统管理员")
    private String bio;
    
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;
    
    @Schema(description = "状态描述", example = "启用")
    private String statusDesc;
    
    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2024-01-01 10:00:00")
    private LocalDateTime updateTime;
}
```

#### 2. 登录响应 DTO

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/dto/response/LoginResponse.java**

```java
package com.ddd.platform.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "登录响应")
public class LoginResponse {
    
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
    
    @Schema(description = "过期时间（秒）", example = "86400")
    private Long expiresIn;
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "昵称", example = "管理员")
    private String nickname;
}
```

### 三、分页封装

#### 1. 分页响应

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/dto/response/PageResponse.java**

```java
package com.ddd.platform.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Schema(description = "分页响应")
public class PageResponse<T> {
    
    @Schema(description = "总记录数", example = "100")
    private Long total;
    
    @Schema(description = "当前页码", example = "1")
    private Integer pageNum;
    
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize;
    
    @Schema(description = "总页数", example = "10")
    private Integer totalPages;
    
    @Schema(description = "数据列表")
    private List<T> records;
    
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;
    
    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;
    
    public PageResponse() {
        this.records = Collections.emptyList();
        this.total = 0L;
        this.pageNum = 1;
        this.pageSize = 10;
        this.totalPages = 0;
        this.hasPrevious = false;
        this.hasNext = false;
    }
    
    public PageResponse(Long total, Integer pageNum, Integer pageSize, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.records = records;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < totalPages;
    }
    
    public static <T> PageResponse<T> of(Long total, Integer pageNum, Integer pageSize, List<T> records) {
        return new PageResponse<>(total, pageNum, pageSize, records);
    }
    
    public static <T> PageResponse<T> empty() {
        return new PageResponse<>();
    }
}
```

### 四、Controller 使用示例

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/controller/UserController.java**

```java
package com.ddd.platform.interfaces.controller;

import com.ddd.platform.application.dto.UserDTO;
import com.ddd.platform.application.service.UserApplicationService;
import com.ddd.platform.common.result.Result;
import com.ddd.platform.interfaces.dto.request.UserLoginRequest;
import com.ddd.platform.interfaces.dto.request.UserPageRequest;
import com.ddd.platform.interfaces.dto.request.UserRegisterRequest;
import com.ddd.platform.interfaces.dto.request.UserUpdateRequest;
import com.ddd.platform.interfaces.dto.response.LoginResponse;
import com.ddd.platform.interfaces.dto.response.PageResponse;
import com.ddd.platform.interfaces.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    
    private final UserApplicationService userApplicationService;
    
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        // 转换请求 DTO 到应用层 DTO
        UserDTO userDTO = userApplicationService.registerUser(convertToCommand(request));
        // 转换应用层 DTO 到响应 DTO
        return Result.success(convertToResponse(userDTO));
    }
    
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        // 登录逻辑
        return Result.success(LoginResponse.builder()
            .accessToken("token")
            .tokenType("Bearer")
            .expiresIn(86400L)
            .userId(1L)
            .username(request.getUsername())
            .build());
    }
    
    @GetMapping("/page")
    @Operation(summary = "分页查询用户")
    public Result<PageResponse<UserResponse>> page(@Valid UserPageRequest request) {
        // 调用应用服务获取分页数据
        // PageResponse<UserResponse> page = userApplicationService.page(request);
        // return Result.success(page);
        return Result.success(PageResponse.empty());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    public Result<UserResponse> getById(@PathVariable Long id) {
        UserDTO userDTO = userApplicationService.getUserById(id);
        return Result.success(convertToResponse(userDTO));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        userApplicationService.updateUser(id, convertToDTO(request));
        return Result.success();
    }
    
    // ========== 转换方法 ==========
    
    private UserDTO convertToDTO(UserUpdateRequest request) {
        UserDTO dto = new UserDTO();
        dto.setEmail(request.getEmail());
        dto.setPhone(request.getPhone());
        dto.setNickname(request.getNickname());
        return dto;
    }
    
    private UserResponse convertToResponse(UserDTO dto) {
        return UserResponse.builder()
            .id(dto.getId())
            .username(dto.getUsername())
            .email(dto.getEmail())
            .phone(dto.getPhone())
            .nickname(dto.getNickname())
            .avatar(dto.getAvatar())
            .status(dto.getStatus())
            .statusDesc(dto.getStatus() == 1 ? "启用" : "禁用")
            .createTime(dto.getCreateTime())
            .build();
    }
    
    private UserDTO convertToCommand(UserRegisterRequest request) {
        UserDTO dto = new UserDTO();
        dto.setUsername(request.getUsername());
        dto.setPassword(request.getPassword());
        dto.setEmail(request.getEmail());
        dto.setPhone(request.getPhone());
        dto.setNickname(request.getNickname());
        return dto;
    }
}
```

### 五、参数校验分组

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/validation/ValidationGroups.java**

```java
package com.ddd.platform.interfaces.validation;

/**
 * 参数校验分组
 */
public interface ValidationGroups {
    
    /**
     * 创建操作
     */
    interface Create {}
    
    /**
     * 更新操作
     */
    interface Update {}
    
    /**
     * 删除操作
     */
    interface Delete {}
    
    /**
     * 查询操作
     */
    interface Query {}
}
```

### 六、使用分组的请求 DTO

**ddd-interfaces/src/main/java/com/ddd/platform/interfaces/dto/request/UserRequest.java**

```java
package com.ddd.platform.interfaces.dto.request;

import com.ddd.platform.interfaces.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "用户请求")
public class UserRequest {
    
    @NotNull(message = "用户ID不能为空", groups = {ValidationGroups.Update.class, ValidationGroups.Delete.class})
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @NotBlank(message = "用户名不能为空", groups = ValidationGroups.Create.class)
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;
    
    @NotBlank(message = "密码不能为空", groups = ValidationGroups.Create.class)
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    @Schema(description = "密码", example = "123456")
    private String password;
    
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}
```

**使用分组的 Controller**

```java
@PostMapping("/create")
public Result<Void> create(@Validated(ValidationGroups.Create.class) @RequestBody UserRequest request) {
    // 创建逻辑
    return Result.success();
}

@PutMapping("/update")
public Result<Void> update(@Validated(ValidationGroups.Update.class) @RequestBody UserRequest request) {
    // 更新逻辑
    return Result.success();
}
```

现在您有了完整的请求/响应 DTO、分页封装和参数校验体系！