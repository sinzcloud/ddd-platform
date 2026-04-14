-- 创建角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
                                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                               `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                               `role_id` BIGINT NOT NULL COMMENT '角色ID',
                                               PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色表';

-- 插入角色数据
INSERT INTO `sys_role` (`role_code`, `role_name`) VALUES
                                                      ('ADMIN', '管理员'),
                                                      ('USER', '普通用户');

-- 给 admin 用户分配 ADMIN 角色（假设 admin 的 id 是 1）
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
    (1, 1);