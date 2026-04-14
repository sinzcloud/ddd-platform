package com.ddd.platform.application.bus;

import com.ddd.platform.application.command.Command;
import com.ddd.platform.application.command.CommandHandler;

/**
 * 命令总线接口
 */
public interface CommandBus {

    /**
     * 发送命令
     */
    <R> R send(Command command);

    /**
     * 异步发送命令
     */
    <R> void sendAsync(Command command);

    /**
     * 注册命令处理器
     */
    <C extends Command, R> void register(Class<C> commandType, CommandHandler<C, R> handler);
}