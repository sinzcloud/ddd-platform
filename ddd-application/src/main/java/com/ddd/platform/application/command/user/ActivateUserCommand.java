package com.ddd.platform.application.command.user;

import com.ddd.platform.application.command.Command;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivateUserCommand implements Command {
    private Long userId;
}