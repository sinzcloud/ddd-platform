package com.ddd.platform.application.query.user;

import com.ddd.platform.application.dto.PageResult;
import com.ddd.platform.application.dto.UserDTO;
import com.ddd.platform.application.query.Query;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageUserQuery implements Query<PageResult<UserDTO>> {
    private Integer pageNum;
    private Integer pageSize;
    private String keyword;
    private Integer status;
}