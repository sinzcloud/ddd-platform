package com.ddd.platform;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAspectJAutoProxy  // 添加此注解
@EnableAsync  // 启用异步支持
@EnableRabbit  // 启用 RabbitMQ
public class BootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapApplication.class, args);
        System.out.println("========================================");
        System.out.println("   DDD Platform Started Successfully!   ");
        System.out.println("   Swagger UI: http://localhost:8080/swagger-ui.html   ");
        System.out.println("========================================");
    }
}