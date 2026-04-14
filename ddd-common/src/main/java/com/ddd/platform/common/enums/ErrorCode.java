package com.ddd.platform.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请重新登录"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "请求资源不存在"),

    // 业务错误 1000-1999
    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_EXIST(1002, "用户名已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_INVALID(1004, "Token无效"),
    TOKEN_EXPIRED(1005, "Token已过期"),

    // 系统错误 2000-2999
    SYSTEM_ERROR(2000, "系统内部错误"),
    DB_ERROR(2001, "数据库异常"),
    REDIS_ERROR(2002, "Redis异常");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}