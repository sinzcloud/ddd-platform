-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ddd_platform` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ddd_platform`;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `lock_status` TINYINT DEFAULT 0 COMMENT '锁定状态：0-未锁定，1-已锁定',
    `fail_count` INT DEFAULT 0 COMMENT '登录失败次数',
    `lock_time` DATETIME NULL COMMENT '锁定时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_phone` (`phone`),
    KEY `idx_create_time` (`create_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户资料表
CREATE TABLE IF NOT EXISTS `sys_user_profile` (
                                                  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                  `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                                  `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(500) COMMENT '头像URL',
    `bio` VARCHAR(200) COMMENT '个人简介',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户资料表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `sys_operation_log` (
                                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                   `module` VARCHAR(50) COMMENT '模块名称',
    `operation` VARCHAR(100) COMMENT '操作描述',
    `type` VARCHAR(20) COMMENT '操作类型',
    `user_id` BIGINT COMMENT '操作人ID',
    `username` VARCHAR(50) COMMENT '操作人名称',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `url` VARCHAR(500) COMMENT '请求URL',
    `method` VARCHAR(10) COMMENT '请求方法',
    `request_params` TEXT COMMENT '请求参数',
    `response_data` TEXT COMMENT '响应数据',
    `cost_time` BIGINT COMMENT '耗时(ms)',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0失败，1成功',
    `error_msg` TEXT COMMENT '错误信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 插入测试数据（密码：123456 的 BCrypt 加密）
INSERT INTO `sys_user` (`username`, `password`, `email`, `status`) VALUES
                                                                       ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@example.com', 1),
                                                                       ('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'test@example.com', 1);

-- 插入用户资料
INSERT INTO `sys_user_profile` (`user_id`, `nickname`, `bio`) VALUES
                                                                  (1, '管理员', '系统管理员'),
                                                                  (2, '测试用户', '这是一个测试用户');

-- 角色表（可选）
CREATE TABLE IF NOT EXISTS `sys_role` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
                                               `id` BIGINT NOT NULL AUTO_INCREMENT,
                                               `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                               `role_id` BIGINT NOT NULL COMMENT '角色ID',
                                               PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色表';

-- 插入角色
INSERT INTO `sys_role` (`role_code`, `role_name`) VALUES
                                                      ('ADMIN', '管理员'),
                                                      ('USER', '普通用户');

-- 给 admin 分配 ADMIN 角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);