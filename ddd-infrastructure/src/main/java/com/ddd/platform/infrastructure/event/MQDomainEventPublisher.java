package com.ddd.platform.infrastructure.event;

import com.ddd.platform.domain.event.DomainEvent;
import com.ddd.platform.domain.event.DomainEventPublisher;
import com.ddd.platform.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Primary
public class MQDomainEventPublisher implements DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(DomainEvent event) {
        String routingKey = getRoutingKey(event);

        try {
            log.info("发送领域事件到MQ: event={}, routingKey={}",
                    event.getClass().getSimpleName(), routingKey);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USER_EVENT_EXCHANGE,
                    routingKey,
                    event
            );

            log.info("MQ消息发送成功");
        } catch (Exception e) {
            log.error("MQ消息发送失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void publishAsync(DomainEvent event) {
        publish(event);
    }

    @Override
    public void publishAll(Iterable<DomainEvent> events) {
        for (DomainEvent event : events) {
            publish(event);
        }
    }

    private String getRoutingKey(DomainEvent event) {
        String eventName = event.getClass().getSimpleName();

        switch (eventName) {
            case "UserCreatedEvent":
                return RabbitMQConfig.USER_CREATED_ROUTING_KEY;
            case "UserActivatedEvent":
                return RabbitMQConfig.USER_ACTIVATED_ROUTING_KEY;
            case "UserDeactivatedEvent":
                return RabbitMQConfig.USER_DEACTIVATED_ROUTING_KEY;
            default:
                return "user.event." + eventName.toLowerCase();
        }
    }
}