package com.ddd.platform.interfaces.controller;

import com.ddd.platform.application.command.RegisterCommand;
import com.ddd.platform.application.dto.UserDTO;
import com.ddd.platform.application.service.UserApplicationService;
import com.ddd.platform.common.result.Result;
import com.ddd.platform.infrastructure.idempotent.Idempotent;
import com.ddd.platform.infrastructure.lock.DistributedLockAnnotation;
import com.ddd.platform.infrastructure.log.OperationLog;
import com.ddd.platform.infrastructure.ratelimit.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、查询、激活等接口")
@Slf4j
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户账号")
    public Result<UserDTO> register(@Valid @RequestBody RegisterCommand command) {
        UserDTO userDTO = userApplicationService.registerUser(command);
        return Result.success(userDTO);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "获取用户信息", description = "根据ID查询用户详情")
    @Idempotent(message = "请勿重复提交订单")
    @RateLimiter(limit = 10, duration = 60)
    public Result<UserDTO> getUserById(@Parameter(description = "用户ID", example = "1") @PathVariable Long userId) {
        UserDTO userDTO = userApplicationService.getUserById(userId);
        return Result.success(userDTO);
    }

    @PutMapping("/{userId}/activate")
    @Operation(summary = "激活用户", description = "管理员操作，激活已禁用的用户")
    @PreAuthorize("hasRole('ADMIN')")
    @DistributedLockAnnotation(key = "'user:update:' + #userId", waitTime = 3, leaseTime = 10)
    @OperationLog(module = "用户管理", operation = "激活用户", type = "UPDATE")
    public Result<Void> activateUser(@Parameter(description = "用户ID", example = "1") @PathVariable Long userId) {
        userApplicationService.activateUser(userId);
        return Result.success();
    }

    @PutMapping("/{userId}/deactivate")
    @Operation(summary = "禁用用户", description = "管理员操作，禁用用户账号")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "用户管理", operation = "禁用用户", type = "UPDATE")
    public Result<Void> deactivateUser(@Parameter(description = "用户ID", example = "1") @PathVariable Long userId) {
        userApplicationService.deactivateUser(userId);
        return Result.success();
    }
}