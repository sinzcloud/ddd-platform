package com.ddd.platform.interfaces.config;

import com.ddd.platform.domain.metrics.MetricsPort;
import com.ddd.platform.infrastructure.metrics.CustomMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MetricsPort metricsPort(CustomMetrics customMetrics) {
        return customMetrics;  // 将实现注入到接口
    }
}