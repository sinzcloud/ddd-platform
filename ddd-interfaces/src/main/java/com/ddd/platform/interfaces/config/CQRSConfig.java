package com.ddd.platform.interfaces.config;

import com.ddd.platform.application.bus.CommandBus;
import com.ddd.platform.application.bus.QueryBus;
import com.ddd.platform.application.command.user.ActivateUserCommand;
import com.ddd.platform.application.command.user.CreateUserCommand;
import com.ddd.platform.application.command.user.DeactivateUserCommand;
import com.ddd.platform.application.handler.user.ActivateUserCommandHandler;
import com.ddd.platform.application.handler.user.CreateUserCommandHandler;
import com.ddd.platform.application.handler.user.DeactivateUserCommandHandler;
import com.ddd.platform.application.handler.user.GetUserByIdQueryHandler;
import com.ddd.platform.application.query.user.GetUserByIdQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CQRSConfig {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    // 命令处理器
    private final CreateUserCommandHandler createUserCommandHandler;
    private final ActivateUserCommandHandler activateUserCommandHandler;
    private final DeactivateUserCommandHandler deactivateUserCommandHandler;

    // 查询处理器
    private final GetUserByIdQueryHandler getUserByIdQueryHandler;

    @PostConstruct
    public void registerHandlers() {
        log.info("========== 开始注册命令处理器 ==========");

        // 注册命令处理器
        commandBus.register(CreateUserCommand.class, createUserCommandHandler);
        commandBus.register(ActivateUserCommand.class, activateUserCommandHandler);
        commandBus.register(DeactivateUserCommand.class, deactivateUserCommandHandler);

        log.info("注册命令处理器: CreateUserCommand -> CreateUserCommandHandler");
        log.info("注册命令处理器: ActivateUserCommand -> ActivateUserCommandHandler");
        log.info("注册命令处理器: DeactivateUserCommand -> DeactivateUserCommandHandler");

        log.info("========== 开始注册查询处理器 ==========");

        // 注册查询处理器
        queryBus.register(GetUserByIdQuery.class, getUserByIdQueryHandler);

        log.info("注册查询处理器: GetUserByIdQuery -> GetUserByIdQueryHandler");

        log.info("========== CQRS 注册完成 ==========");
    }
}