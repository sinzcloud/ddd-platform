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