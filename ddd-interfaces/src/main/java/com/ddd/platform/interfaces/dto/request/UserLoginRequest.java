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