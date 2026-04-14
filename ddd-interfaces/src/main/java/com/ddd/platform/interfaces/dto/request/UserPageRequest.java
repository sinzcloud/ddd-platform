package com.ddd.platform.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户分页查询请求")
public class UserPageRequest extends PageRequest {

    @Schema(description = "用户名关键词", example = "admin")
    private String username;

    @Schema(description = "邮箱关键词", example = "@example.com")
    private String email;

    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    private String startTime;

    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    private String endTime;
}