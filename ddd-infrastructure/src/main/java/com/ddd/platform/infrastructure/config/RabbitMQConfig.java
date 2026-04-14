package com.ddd.platform.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EVENT_EXCHANGE = "user.event.exchange";
    public static final String USER_CREATED_QUEUE = "user.created.queue";
    public static final String USER_ACTIVATED_QUEUE = "user.activated.queue";
    public static final String USER_DEACTIVATED_QUEUE = "user.deactivated.queue";

    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String USER_ACTIVATED_ROUTING_KEY = "user.activated";
    public static final String USER_DEACTIVATED_ROUTING_KEY = "user.deactivated";

    @Bean
    public TopicExchange userEventExchange() {
        return ExchangeBuilder.topicExchange(USER_EVENT_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder.durable(USER_CREATED_QUEUE).build();
    }

    @Bean
    public Queue userActivatedQueue() {
        return QueueBuilder.durable(USER_ACTIVATED_QUEUE).build();
    }

    @Bean
    public Queue userDeactivatedQueue() {
        return QueueBuilder.durable(USER_DEACTIVATED_QUEUE).build();
    }

    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(userCreatedQueue()).to(userEventExchange()).with(USER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding userActivatedBinding() {
        return BindingBuilder.bind(userActivatedQueue()).to(userEventExchange()).with(USER_ACTIVATED_ROUTING_KEY);
    }

    @Bean
    public Binding userDeactivatedBinding() {
        return BindingBuilder.bind(userDeactivatedQueue()).to(userEventExchange()).with(USER_DEACTIVATED_ROUTING_KEY);
    }

    /**
     * 配置支持 Java 8 时间类型的 ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * JSON 消息转换器 - 关键：使用配置好的 ObjectMapper
     */
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setCreateMessageIds(true);
        return converter;
    }

    /**
     * RabbitTemplate - 使用配置好的 MessageConverter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    /**
     * 监听器容器工厂 - 使用配置好的 MessageConverter
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}