package com.ddd.platform.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {

    private String token;
    private String tokenType;
    private Long userId;
    private String username;
    private Long expiresIn;
}