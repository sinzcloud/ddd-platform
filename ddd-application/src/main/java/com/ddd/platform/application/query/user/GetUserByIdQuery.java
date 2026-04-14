package com.ddd.platform.application.query.user;

import com.ddd.platform.application.dto.UserDTO;
import com.ddd.platform.application.query.Query;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserByIdQuery implements Query<UserDTO> {
    private Long userId;
}