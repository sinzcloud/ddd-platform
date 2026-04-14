package com.ddd.platform.interfaces.controller;

import com.ddd.platform.common.result.Result;
import com.ddd.platform.domain.user.entity.User;
import com.ddd.platform.domain.user.service.UserDomainService;
import com.ddd.platform.domain.user.valueobject.Email;
import com.ddd.platform.domain.user.valueobject.PhoneNumber;
import com.ddd.platform.domain.user.valueobject.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test/ddd")
@RequiredArgsConstructor
@Tag(name = "DDD测试", description = "测试值对象、领域服务、领域事件")
public class TestDDDController {

    private final UserDomainService userDomainService;

    /**
     * 测试值对象 - Email
     */
    @GetMapping("/value-object/email")
    @Operation(summary = "测试Email值对象")
    public Result<Map<String, Object>> testEmailValueObject(@RequestParam String emailStr) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 创建Email值对象
            Email email = new Email(emailStr);

            result.put("success", true);
            result.put("email", email.getValue());
            result.put("domain", email.getDomain());
            result.put("username", email.getUsername());
            result.put("equals", email.equals(new Email(emailStr)));
            result.put("hashCode", email.hashCode());

            log.info("Email值对象测试: {}", email);

        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return Result.success(result);
    }

    /**
     * 测试值对象 - PhoneNumber
     */
    @GetMapping("/value-object/phone")
    @Operation(summary = "测试PhoneNumber值对象")
    public Result<Map<String, Object>> testPhoneValueObject(@RequestParam String phoneStr) {
        Map<String, Object> result = new HashMap<>();

        try {
            PhoneNumber phone = new PhoneNumber(phoneStr);

            result.put("success", true);
            result.put("phone", phone.getValue());
            result.put("masked", phone.getMasked());
            result.put("fullNumber", phone.getFullNumber());

            log.info("PhoneNumber值对象测试: {}", phone);

        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return Result.success(result);
    }

    /**
     * 测试值对象 - 相等性比较
     */
    @GetMapping("/value-object/equality")
    @Operation(summary = "测试值对象相等性")
    public Result<Map<String, Boolean>> testValueObjectEquality() {
        Map<String, Boolean> result = new HashMap<>();

        // Email相等性测试
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");
        Email email3 = new Email("test3@example.com");

        result.put("email1.equals(email2)", email1.equals(email2));
        result.put("email1.equals(email3)", email1.equals(email3));

        // PhoneNumber相等性测试
        PhoneNumber phone1 = new PhoneNumber("13800138000");
        PhoneNumber phone2 = new PhoneNumber("13800138000");
        PhoneNumber phone3 = new PhoneNumber("13900139000");

        result.put("phone1.equals(phone2)", phone1.equals(phone2));
        result.put("phone1.equals(phone3)", phone1.equals(phone3));

        return Result.success(result);
    }

    /**
     * 测试领域服务 - 创建用户
     */
    @PostMapping("/domain-service/create-user")
    @Operation(summary = "测试领域服务创建用户")
    public Result<Map<String, Object>> testCreateUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam(defaultValue = "测试用户") String nickname) {

        Map<String, Object> result = new HashMap<>();

        try {
            // 调用领域服务创建用户
            var userAggregate = userDomainService.createUser(username, password, email, nickname);

            result.put("success", true);
            result.put("userId", userAggregate.getUserId());
            result.put("username", userAggregate.getUsername());
            result.put("email", userAggregate.getEmail());
            result.put("nickname", userAggregate.getNickname());
            result.put("isActive", userAggregate.isActive());

            log.info("领域服务创建用户成功: userId={}", userAggregate.getUserId());

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return Result.success(result);
    }

    /**
     * 测试领域服务 - 激活用户
     */
    @PostMapping("/domain-service/activate-user")
    @Operation(summary = "测试领域服务激活用户")
    public Result<Map<String, Object>> testActivateUser(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            User user = userDomainService.activateUser(new UserId(userId));

            result.put("success", true);
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("status", user.getStatus());
            result.put("isActive", user.isActive());

            log.info("领域服务激活用户成功: userId={}", userId);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return Result.success(result);
    }

    /**
     * 测试领域服务 - 验证用户凭证
     */
    @PostMapping("/domain-service/validate-credentials")
    @Operation(summary = "测试领域服务验证凭证")
    public Result<Map<String, Object>> testValidateCredentials(
            @RequestParam String username,
            @RequestParam String password) {

        Map<String, Object> result = new HashMap<>();

        try {
            User user = userDomainService.validateCredentials(username, password);

            result.put("success", true);
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("isActive", user.isActive());
            result.put("isLocked", user.isLocked());

            log.info("领域服务验证凭证成功: username={}", username);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return Result.success(result);
    }

    /**
     * 测试领域事件 - 手动触发事件
     */
    @PostMapping("/domain-event/trigger-events")
    @Operation(summary = "测试领域事件触发")
    public Result<Map<String, Object>> testDomainEvents() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 创建一个用户会触发多个事件
            var userAggregate = userDomainService.createUser(
                    "event_test_" + System.currentTimeMillis(),
                    "123456",
                    "event_test_" + System.currentTimeMillis() + "@test.com",
                    "事件测试用户"
            );

            result.put("success", true);
            result.put("userId", userAggregate.getUserId());
            result.put("message", "用户创建成功，已触发 UserCreatedEvent");
            result.put("events", "请查看日志或 sys_operation_log 表");

            log.info("领域事件测试: 用户创建事件已触发");

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return Result.success(result);
    }
}