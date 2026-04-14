package com.ddd.platform.domain.user.event;

import com.ddd.platform.domain.event.BaseDomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor  // 添加无参构造函数
public class UserActivatedEvent extends BaseDomainEvent {

    private String username;
    private LocalDateTime activatedAt;

    public UserActivatedEvent(String aggregateId, String username) {
        super(aggregateId);
        this.username = username;
        this.activatedAt = LocalDateTime.now();
    }
}