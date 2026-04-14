package com.ddd.platform.infrastructure.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLockAnnotation {

    String key();                    // 锁的key，支持SpEL表达式
    long waitTime() default 3;      // 等待时间
    long leaseTime() default 10;     // 持有时间
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}