-- 删除已存在的表
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user_profile;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_operation_log;

-- 创建用户表
CREATE TABLE sys_user (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          username VARCHAR(50) NOT NULL,
                          password VARCHAR(100) NOT NULL,
                          email VARCHAR(100),
                          phone VARCHAR(20),
                          status TINYINT DEFAULT 1,
                          lock_status TINYINT DEFAULT 0,
                          fail_count INT DEFAULT 0,
                          lock_time TIMESTAMP,
                          deleted TINYINT DEFAULT 0,
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建用户资料表
CREATE TABLE sys_user_profile (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  user_id BIGINT NOT NULL,
                                  nickname VARCHAR(50),
                                  avatar VARCHAR(500),
                                  bio VARCHAR(200),
                                  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建角色表
CREATE TABLE sys_role (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          role_code VARCHAR(50) NOT NULL,
                          role_name VARCHAR(50) NOT NULL,
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户角色关联表
CREATE TABLE sys_user_role (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               role_id BIGINT NOT NULL
);

-- 创建操作日志表
CREATE TABLE sys_operation_log (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   module VARCHAR(50),
                                   operation VARCHAR(100),
                                   type VARCHAR(20),
                                   user_id BIGINT,
                                   username VARCHAR(50),
                                   ip VARCHAR(50),
                                   url VARCHAR(500),
                                   method VARCHAR(10),
                                   request_params TEXT,
                                   response_data TEXT,
                                   cost_time BIGINT,
                                   status TINYINT DEFAULT 1,
                                   error_msg TEXT,
                                   create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入角色数据
INSERT INTO sys_role (id, role_code, role_name) VALUES
                                                    (1, 'ADMIN', '管理员'),
                                                    (2, 'USER', '普通用户');

-- 插入测试用户数据
INSERT INTO sys_user (id, username, password, email, status) VALUES
                                                                 (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@example.com', 1),
                                                                 (2, 'test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'test@example.com', 1);

-- 插入用户资料
INSERT INTO sys_user_profile (user_id, nickname) VALUES
                                                     (1, '管理员'),
                                                     (2, '测试用户');

-- 插入用户角色关联（给 admin 分配 ADMIN 角色）
INSERT INTO sys_user_role (user_id, role_id) VALUES
                                                 (1, 1),
                                                 (2, 2);