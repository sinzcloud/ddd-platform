package com.ddd.platform.interfaces.bus;

import com.ddd.platform.application.bus.CommandBus;
import com.ddd.platform.application.command.Command;
import com.ddd.platform.application.command.CommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SimpleCommandBus implements CommandBus {

    private final Map<Class<?>, CommandHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <R> R send(Command command) {
        CommandHandler<Command, R> handler = (CommandHandler<Command, R>) handlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for command: " + command.getClass().getName());
        }

        log.debug("执行命令: {}", command.getClass().getSimpleName());
        long startTime = System.currentTimeMillis();

        try {
            R result = handler.handle(command);
            long costTime = System.currentTimeMillis() - startTime;
            log.debug("命令执行完成: {}, 耗时: {}ms", command.getClass().getSimpleName(), costTime);
            return result;
        } catch (Exception e) {
            log.error("命令执行失败: {}", command.getClass().getSimpleName(), e);
            throw e;
        }
    }

    @Override
    @Async
    public <R> void sendAsync(Command command) {
        send(command);
    }

    @Override
    public <C extends Command, R> void register(Class<C> commandType, CommandHandler<C, R> handler) {
        handlers.put(commandType, handler);
        log.info("注册命令处理器: {} -> {}", commandType.getSimpleName(), handler.getClass().getSimpleName());
    }
}