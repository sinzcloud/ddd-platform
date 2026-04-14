package com.ddd.platform.domain.metrics;

/**
 * 监控端口（定义在领域层）
 */
public interface MetricsPort {

    void recordUserRegister();

    void recordUserLogin();

    void recordLoginFailure();
}