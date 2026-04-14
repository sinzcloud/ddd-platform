package com.ddd.platform.interfaces.controller;

import com.ddd.platform.application.bus.CommandBus;
import com.ddd.platform.application.bus.QueryBus;
import com.ddd.platform.application.command.user.*;
import com.ddd.platform.application.dto.UserDTO;
import com.ddd.platform.application.query.user.GetUserByIdQuery;
import com.ddd.platform.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cqrs/users")
@RequiredArgsConstructor
@Tag(name = "CQRS用户管理", description = "基于CQRS的用户管理接口")
public class UserCQRSController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @PostMapping
    @Operation(summary = "创建用户（命令）")
    public Result<UserDTO> createUser(@RequestBody CreateUserCommand command) {
        UserDTO result = commandBus.send(command);
        return Result.success(result);
    }

    @PutMapping("/{userId}/activate")
    @Operation(summary = "激活用户（命令）")
    public Result<Void> activateUser(@PathVariable Long userId) {
        ActivateUserCommand command = ActivateUserCommand.builder()
                .userId(userId)
                .build();
        commandBus.send(command);
        return Result.success();
    }

    @PutMapping("/{userId}/deactivate")
    @Operation(summary = "禁用用户（命令）")
    public Result<Void> deactivateUser(@PathVariable Long userId) {
        DeactivateUserCommand command = DeactivateUserCommand.builder()
                .userId(userId)
                .build();
        commandBus.send(command);
        return Result.success();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "查询用户（查询）")
    public Result<UserDTO> getUserById(@PathVariable Long userId) {
        GetUserByIdQuery query = GetUserByIdQuery.builder()
                .userId(userId)
                .build();
        UserDTO result = queryBus.send(query);
        return Result.success(result);
    }
}