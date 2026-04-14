package com.ddd.platform.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "基础请求参数")
public class BaseRequest implements Serializable {

    @Schema(description = "请求ID", example = "req_123456")
    private String requestId;

    @Schema(description = "客户端版本", example = "1.0.0")
    private String version = "1.0.0";

    @Schema(description = "时间戳", example = "1702364207900")
    private Long timestamp = System.currentTimeMillis();
}