package com.ddd.platform.infrastructure.listener;

import com.ddd.platform.domain.user.event.*;
import com.ddd.platform.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final RedisService redisService;

    /**
     * 用户创建事件 - 事务提交后执行
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("处理用户创建事件: userId={}, username={}",
                event.getAggregateId(), event.getUsername());

        // 1. 缓存用户信息
        String cacheKey = "user:username:" + event.getUsername();
        redisService.set(cacheKey, event.getAggregateId(), 3600L);

        // 2. 发送欢迎邮件
        sendWelcomeEmail(event.getEmail());

        // 3. 初始化用户配置
        initUserConfig(event.getAggregateId());

        // 4. 记录审计日志
        saveAuditLog("用户创建", event.getUsername());
    }

    /**
     * 用户激活事件
     */
    @Async
    @EventListener
    public void handleUserActivated(UserActivatedEvent event) {
        log.info("处理用户激活事件: userId={}, username={}",
                event.getAggregateId(), event.getUsername());

        // 1. 更新缓存状态
        String cacheKey = "user:status:" + event.getAggregateId();
        redisService.set(cacheKey, "ACTIVATED", 3600L);

        // 2. 发送激活通知
        sendActivationNotification(event.getUsername());
    }

    /**
     * 用户禁用事件
     */
    @Async
    @EventListener
    public void handleUserDeactivated(UserDeactivatedEvent event) {
        log.info("处理用户禁用事件: userId={}, username={}, reason={}",
                event.getAggregateId(), event.getUsername(), event.getReason());

        // 1. 清除缓存
        String cacheKey = "user:username:" + event.getUsername();
        redisService.delete(cacheKey);

        // 2. 清除Token
        String tokenKey = "token:" + event.getUsername();
        redisService.delete(tokenKey);

        // 3. 记录安全日志
        saveSecurityLog("用户禁用", event.getUsername(), event.getReason());
    }

    /**
     * 邮箱更新事件
     */
    @Async
    @EventListener
    public void handleEmailUpdated(UserEmailUpdatedEvent event) {
        log.info("处理邮箱更新事件: userId={}, oldEmail={}, newEmail={}",
                event.getAggregateId(), event.getOldEmail(), event.getNewEmail());

        // 1. 发送邮箱变更确认邮件
        sendEmailChangeConfirmation(event.getNewEmail());

        // 2. 记录审计日志
        saveAuditLog("邮箱变更",
                String.format("从 %s 变更为 %s", event.getOldEmail(), event.getNewEmail()));
    }

    /**
     * 密码变更事件
     */
    @Async
    @EventListener
    public void handlePasswordChanged(UserPasswordChangedEvent event) {
        log.info("处理密码变更事件: userId={}, username={}",
                event.getAggregateId(), event.getUsername());

        // 1. 清除所有Token
        String tokenKey = "token:" + event.getUsername();
        redisService.delete(tokenKey);

        // 2. 发送密码变更通知
        sendPasswordChangeNotification(event.getUsername());

        // 3. 记录安全日志
        saveSecurityLog("密码变更", event.getUsername(), "密码已修改");
    }

    private void sendWelcomeEmail(String email) {
        // 调用邮件服务
        log.info("发送欢迎邮件到: {}", email);
    }

    private void initUserConfig(String userId) {
        // 初始化用户配置
        log.info("初始化用户配置: {}", userId);
    }

    private void saveAuditLog(String operation, String detail) {
        // 保存审计日志
        log.info("审计日志: {} - {}", operation, detail);
    }

    private void saveSecurityLog(String operation, String username, String detail) {
        // 保存安全日志
        log.info("安全日志: {} - {} - {}", operation, username, detail);
    }

    private void sendActivationNotification(String username) {
        log.info("发送激活通知给: {}", username);
    }

    private void sendEmailChangeConfirmation(String email) {
        log.info("发送邮箱变更确认到: {}", email);
    }

    private void sendPasswordChangeNotification(String username) {
        log.info("发送密码变更通知给: {}", username);
    }
}