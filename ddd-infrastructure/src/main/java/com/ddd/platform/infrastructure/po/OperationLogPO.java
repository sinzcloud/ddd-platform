package com.ddd.platform.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class OperationLogPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String module;
    private String operation;
    private String type;
    private Long userId;
    private String username;
    private String ip;
    private String url;
    private String method;
    private String requestParams;
    private String responseData;
    private Long costTime;
    private Integer status;
    private String errorMsg;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}