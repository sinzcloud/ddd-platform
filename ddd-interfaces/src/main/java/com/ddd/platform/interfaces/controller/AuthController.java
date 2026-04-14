package com.ddd.platform.interfaces.controller;

import com.ddd.platform.application.command.LoginCommand;
import com.ddd.platform.application.dto.LoginResponseDTO;
import com.ddd.platform.application.service.AuthApplicationService;
import com.ddd.platform.domain.metrics.MetricsPort;
import com.ddd.platform.infrastructure.service.TokenService;
import com.ddd.platform.common.constant.Constants;
import com.ddd.platform.common.enums.ErrorCode;
import com.ddd.platform.common.result.Result;
import com.ddd.platform.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final TokenService tokenService;
    private final MetricsPort metricsPort;  // 通过接口注入

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponseDTO> login(@Valid @RequestBody LoginCommand command) {
        log.info("登录请求: username={}", command.getUsername());

        // 验证用户凭证 - 使用正确的方法名 validateCredentials
        User user = authApplicationService.validateCredentials(command.getUsername(), command.getPassword());

        // 生成Token
        String token = tokenService.generateToken(command.getUsername(), user.getId());

        LoginResponseDTO response = LoginResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(command.getUsername())
                .expiresIn(24 * 60 * 60L)
                .build();

        log.info("登录成功: username={}, userId={}", command.getUsername(), user.getId());
        return Result.success(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader(Constants.AUTHORIZATION_HEADER);
        tokenService.invalidateToken(token);
        log.info("用户登出成功");
        return Result.success();
    }

    @GetMapping("/validate")
    @Operation(summary = "验证Token")
    public Result<Boolean> validateToken(HttpServletRequest request) {
        String token = request.getHeader(Constants.AUTHORIZATION_HEADER);
        boolean isValid = tokenService.validateToken(token);
        return Result.success(isValid);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token")
    public Result<LoginResponseDTO> refreshToken(HttpServletRequest request) {
        String oldToken = request.getHeader(Constants.AUTHORIZATION_HEADER);
        String newToken = tokenService.refreshToken(oldToken);

        String username = tokenService.extractUsername(newToken);
        Long userId = tokenService.extractUserId(newToken);

        LoginResponseDTO response = LoginResponseDTO.builder()
                .token(newToken)
                .tokenType("Bearer")
                .userId(userId)
                .username(username)
                .expiresIn(24 * 60 * 60L)
                .build();

        return Result.success(response);
    }

    @GetMapping("/current-user")
    @Operation(summary = "获取当前登录用户信息")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader(Constants.AUTHORIZATION_HEADER);

        if (!tokenService.validateToken(token)) {
            return Result.error(ErrorCode.UNAUTHORIZED.getCode(), "未登录或Token已失效");
        }

        String username = tokenService.extractUsername(token);
        Long userId = tokenService.extractUserId(token);

        User user = authApplicationService.getUserInfo(userId);
        if (user != null) {
            user.setPassword(null); // 清除敏感信息
            user.setFailCount(null);
            user.setLockStatus(null);
        }

        return Result.success(user);
    }

    @GetMapping("/user-info")
    @Operation(summary = "根据用户名获取用户信息")
    public Result<User> getUserByUsername(@RequestParam String username) {
        User user = authApplicationService.getUserByUsername(username);
        if (user != null) {
            user.setPassword(null);
            user.setFailCount(null);
            user.setLockStatus(null);
        }
        return Result.success(user);
    }

    @GetMapping("/check-exists")
    @Operation(summary = "检查用户是否存在")
    public Result<Boolean> checkUserExists(@RequestParam String username) {
        boolean exists = authApplicationService.checkUserExists(username);
        return Result.success(exists);
    }
}