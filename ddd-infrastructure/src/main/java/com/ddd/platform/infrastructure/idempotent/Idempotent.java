package com.ddd.platform.infrastructure.idempotent;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 幂等key，支持SpEL表达式
     */
    String key() default "";

    /**
     * 过期时间，默认60秒
     */
    int timeout() default 60;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 提示消息
     */
    String message() default "重复请求，请勿重复提交";
}