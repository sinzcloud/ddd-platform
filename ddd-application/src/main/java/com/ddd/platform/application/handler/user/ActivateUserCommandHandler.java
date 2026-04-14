package com.ddd.platform.application.handler.user;

import com.ddd.platform.application.command.user.ActivateUserCommand;
import com.ddd.platform.application.command.CommandHandler;
import com.ddd.platform.application.service.UserApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivateUserCommandHandler implements CommandHandler<ActivateUserCommand, Void> {

    private final UserApplicationService userApplicationService;

    @Override
    public Void handle(ActivateUserCommand command) {
        userApplicationService.activateUser(command.getUserId());
        return null;
    }
}