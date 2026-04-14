package com.ddd.platform.infrastructure.ratelimit;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流key，支持SpEL表达式
     */
    String key() default "";

    /**
     * 限制次数，默认10次
     */
    int limit() default 10;

    /**
     * 时间窗口，默认60秒
     */
    int duration() default 60;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 提示消息
     */
    String message() default "请求过于频繁，请稍后再试";
}