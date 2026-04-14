package com.ddd.platform.domain.event;

public interface DomainEventPublisher {

    /**
     * 发布领域事件
     */
    void publish(DomainEvent event);

    /**
     * 异步发布领域事件
     */
    void publishAsync(DomainEvent event);

    /**
     * 批量发布领域事件
     */
    void publishAll(Iterable<DomainEvent> events);
}