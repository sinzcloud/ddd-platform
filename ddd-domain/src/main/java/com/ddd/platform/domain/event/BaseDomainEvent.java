package com.ddd.platform.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public abstract class BaseDomainEvent implements DomainEvent, Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId;
    private String aggregateId;
    private String eventType;
    private LocalDateTime occurredAt;
    private int version;

    public BaseDomainEvent(String aggregateId) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.eventType = this.getClass().getSimpleName();
        this.occurredAt = LocalDateTime.now();
        this.version = 1;
    }

    public BaseDomainEvent(String aggregateId, int version) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.eventType = this.getClass().getSimpleName();
        this.occurredAt = LocalDateTime.now();
        this.version = version;
    }
}