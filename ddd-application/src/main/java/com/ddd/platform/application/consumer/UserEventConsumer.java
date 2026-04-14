package com.ddd.platform.application.consumer;

import com.ddd.platform.domain.user.event.UserActivatedEvent;
import com.ddd.platform.domain.user.event.UserCreatedEvent;
import com.ddd.platform.domain.user.event.UserDeactivatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    @RabbitListener(queues = "user.created.queue")
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("【MQ消费】用户创建事件: userId={}, username={}",
                event.getAggregateId(), event.getUsername());

        // 处理业务逻辑
        // 1. 发送欢迎邮件
        // 2. 初始化用户配置
        // 3. 记录审计日志
    }

    @RabbitListener(queues = "user.activated.queue")
    public void handleUserActivated(UserActivatedEvent event) {
        log.info("【MQ消费】用户激活事件: userId={}, username={}",
                event.getAggregateId(), event.getUsername());

        // 处理业务逻辑
        // 1. 发送激活通知
        // 2. 更新缓存
    }

    @RabbitListener(queues = "user.deactivated.queue")
    public void handleUserDeactivated(UserDeactivatedEvent event) {
        log.info("【MQ消费】用户禁用事件: userId={}, username={}",
                event.getAggregateId(), event.getUsername());

        // 处理业务逻辑
        // 1. 清除用户缓存
        // 2. 清除用户Token
    }
}