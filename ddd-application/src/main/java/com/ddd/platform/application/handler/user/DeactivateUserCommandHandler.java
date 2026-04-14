package com.ddd.platform.application.handler.user;

import com.ddd.platform.application.command.CommandHandler;
import com.ddd.platform.application.command.user.DeactivateUserCommand;
import com.ddd.platform.application.service.UserApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeactivateUserCommandHandler implements CommandHandler<DeactivateUserCommand, Void> {

    private final UserApplicationService userApplicationService;

    @Override
    public Void handle(DeactivateUserCommand command) {
        userApplicationService.deactivateUser(command.getUserId());
        return null;
    }
}