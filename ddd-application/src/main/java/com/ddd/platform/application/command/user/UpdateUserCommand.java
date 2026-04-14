package com.ddd.platform.application.command.user;

import com.ddd.platform.application.command.Command;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserCommand implements Command {
    private Long userId;
    private String email;
    private String phone;
    private String nickname;
    private String avatar;
}