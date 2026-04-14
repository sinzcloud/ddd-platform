package com.ddd.platform.infrastructure.ratelimit;

public enum LimitType {
    /**
     * 默认策略，基于IP限流
     */
    DEFAULT,
    /**
     * 基于用户ID限流
     */
    USER,
    /**
     * 基于接口限流
     */
    API
}