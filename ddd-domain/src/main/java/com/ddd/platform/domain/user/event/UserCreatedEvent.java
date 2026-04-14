package com.ddd.platform.domain.user.event;

import com.ddd.platform.domain.event.BaseDomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCreatedEvent extends BaseDomainEvent {

    private String username;
    private String email;
    private String nickname;

    public UserCreatedEvent(String aggregateId, String username, String email, String nickname) {
        super(aggregateId);
        this.username = username;
        this.email = email;
        this.nickname = nickname;
    }
}