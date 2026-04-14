package com.ddd.platform.domain.user.event;

import com.ddd.platform.domain.event.BaseDomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDeactivatedEvent extends BaseDomainEvent {

    private String username;
    private String reason;

    public UserDeactivatedEvent(String aggregateId, String username, String reason) {
        super(aggregateId);
        this.username = username;
        this.reason = reason;
    }
}