package com.ddd.platform.domain.event;

import java.time.LocalDateTime;

/**
 * 领域事件接口
 */
public interface DomainEvent {

    /**
     * 事件ID
     */
    String getEventId();

    /**
     * 聚合根ID
     */
    String getAggregateId();

    /**
     * 事件类型
     */
    String getEventType();

    /**
     * 发生时间
     */
    LocalDateTime getOccurredAt();

    /**
     * 事件版本
     */
    int getVersion();
}