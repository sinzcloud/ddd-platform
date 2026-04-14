package com.ddd.platform.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class UserPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer status;

    /**
     * 锁定状态：0-未锁定，1-已锁定
     */
    @TableField(value = "lock_status")
    private Integer lockStatus;

    /**
     * 登录失败次数
     */
    @TableField(value = "fail_count")
    private Integer failCount;

    /**
     * 锁定时间
     */
    @TableField(value = "lock_time")
    private LocalDateTime lockTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志（0-未删除，1-已删除）
     */
    @TableLogic
    @TableField(value = "deleted")
    private Integer deleted;
}