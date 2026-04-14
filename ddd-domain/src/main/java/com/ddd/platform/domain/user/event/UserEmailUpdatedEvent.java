package com.ddd.platform.domain.user.event;

import com.ddd.platform.domain.event.BaseDomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserEmailUpdatedEvent extends BaseDomainEvent {

    private String oldEmail;
    private String newEmail;

    public UserEmailUpdatedEvent(String aggregateId, String oldEmail, String newEmail) {
        super(aggregateId);
        this.oldEmail = oldEmail;
        this.newEmail = newEmail;
    }
}