package com.ddd.platform.application.handler.user;

import com.ddd.platform.application.dto.UserDTO;
import com.ddd.platform.application.query.QueryHandler;
import com.ddd.platform.application.query.user.GetUserByIdQuery;
import com.ddd.platform.application.service.UserApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetUserByIdQueryHandler implements QueryHandler<GetUserByIdQuery, UserDTO> {

    private final UserApplicationService userApplicationService;

    @Override
    public UserDTO handle(GetUserByIdQuery query) {
        return userApplicationService.getUserById(query.getUserId());
    }
}