package com.ddd.platform.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "分页请求")
public class PageRequest extends BaseRequest {

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页条数最大为100")
    @Schema(description = "每页条数", example = "10", defaultValue = "10")
    private Integer pageSize = 10;

    @Schema(description = "排序字段", example = "createTime")
    private String orderBy;

    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String orderDirection = "DESC";
}