package com.ddd.platform.infrastructure.log;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 模块名称
     */
    String module();

    /**
     * 操作描述
     */
    String operation();

    /**
     * 操作类型：INSERT/UPDATE/DELETE/SELECT/EXPORT/LOGIN/LOGOUT
     */
    String type() default "OTHER";

    /**
     * 是否保存请求参数
     */
    boolean saveParam() default true;

    /**
     * 是否保存返回结果
     */
    boolean saveResult() default false;
}