package com.ddd.platform.domain.log.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLogEntity {
    private Long id;
    private String module;       // 模块
    private String operation;    // 操作
    private String type;         // 类型
    private Long userId;         // 操作人ID
    private String username;     // 操作人名称
    private String ip;           // IP地址
    private String url;          // 请求URL
    private String method;       // 请求方法
    private String requestParams;// 请求参数
    private String responseData; // 响应数据
    private Long costTime;       // 耗时(ms)
    private Integer status;      // 状态：0失败，1成功
    private String errorMsg;     // 错误信息
    private LocalDateTime createTime;
}