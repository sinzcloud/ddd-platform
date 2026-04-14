package com.ddd.platform.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user_profile")
public class UserProfilePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String nickname;
    private String avatar;
    private String bio;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}