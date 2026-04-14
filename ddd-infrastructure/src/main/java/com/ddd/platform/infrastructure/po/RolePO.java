package com.ddd.platform.infrastructure.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role")
public class RolePO {
    private Long id;
    private String roleCode;
    private String roleName;
}