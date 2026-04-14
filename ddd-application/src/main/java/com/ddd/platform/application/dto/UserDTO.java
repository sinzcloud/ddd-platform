package com.ddd.platform.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private Integer status;
    private String nickname;
    private String avatar;
    private LocalDateTime createTime;
}