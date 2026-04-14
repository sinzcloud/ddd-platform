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