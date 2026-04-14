package com.ddd.platform.infrastructure.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_role")
public class UserRolePO {
    private Long id;
    private Long userId;
    private Long roleId;
}