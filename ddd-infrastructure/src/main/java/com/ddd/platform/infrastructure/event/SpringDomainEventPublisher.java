package com.ddd.platform.infrastructure.event;

import com.ddd.platform.domain.event.DomainEvent;
import com.ddd.platform.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.debug("发布领域事件(同步): eventType={}, eventId={}",
                event.getEventType(), event.getEventId());
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    @Async
    public void publishAsync(DomainEvent event) {
        log.debug("发布领域事件(异步): eventType={}, eventId={}",
                event.getEventType(), event.getEventId());
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishAll(Iterable<DomainEvent> events) {
        for (DomainEvent event : events) {
            publishAsync(event);
        }
    }
}