package com.ddd.platform.interfaces.controller;

import com.ddd.platform.common.result.Result;
import com.ddd.platform.infrastructure.idempotent.IdempotentTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/idempotent")
@RequiredArgsConstructor
@Tag(name = "幂等性", description = "幂等Token管理接口")
public class IdempotentController {

    private final IdempotentTokenService idempotentTokenService;

    @GetMapping("/token")
    @Operation(summary = "获取幂等Token")
    public Result<Map<String, String>> getToken() {
        String token = idempotentTokenService.generateToken();
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("expiresIn", "60");
        return Result.success(data);
    }

    @GetMapping("/token/{businessKey}")
    @Operation(summary = "获取业务幂等Token")
    public Result<Map<String, String>> getTokenWithBusinessKey(@PathVariable String businessKey) {
        String token = idempotentTokenService.generateToken(businessKey);
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("businessKey", businessKey);
        data.put("expiresIn", "60");
        return Result.success(data);
    }
}