package com.ddd.platform.domain.user.event;

import com.ddd.platform.domain.event.BaseDomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserPasswordChangedEvent extends BaseDomainEvent {

    private String username;
    private LocalDateTime changedAt;

    public UserPasswordChangedEvent(String aggregateId, String username) {
        super(aggregateId);
        this.username = username;
        this.changedAt = LocalDateTime.now();
    }
}