package com.ddd.platform.application.handler.user;

import com.ddd.platform.application.command.RegisterCommand;
import com.ddd.platform.application.command.user.CreateUserCommand;
import com.ddd.platform.application.command.CommandHandler;
import com.ddd.platform.application.dto.UserDTO;
import com.ddd.platform.application.service.UserApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand, UserDTO> {

    private final UserApplicationService userApplicationService;

    @Override
    public UserDTO handle(CreateUserCommand command) {
        // 转换为 RegisterCommand
        RegisterCommand registerCommand = new RegisterCommand();
        registerCommand.setUsername(command.getUsername());
        registerCommand.setPassword(command.getPassword());
        registerCommand.setEmail(command.getEmail());
        registerCommand.setNickname(command.getNickname());
        registerCommand.setPhone(command.getPhone());

        return userApplicationService.registerUser(registerCommand);
    }
}