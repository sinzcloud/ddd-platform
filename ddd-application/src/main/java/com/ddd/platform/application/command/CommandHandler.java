package com.ddd.platform.application.command;

/**
 * 命令处理器接口
 */
@FunctionalInterface
public interface CommandHandler<C extends Command, R> {
    R handle(C command);
}