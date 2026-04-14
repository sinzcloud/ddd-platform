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
    KEY `idx_create_time` (`create_time`),
    KEY `idx_module` (`module`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';