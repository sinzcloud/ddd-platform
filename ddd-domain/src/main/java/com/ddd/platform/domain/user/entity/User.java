package com.ddd.platform.domain.user.entity;

import com.ddd.platform.domain.entity.BaseEntity;
import com.ddd.platform.domain.user.event.*;
import com.ddd.platform.domain.user.valueobject.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 * 用户实体（聚合根）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码（BCrypt加密） */
    private String password;

    /** 邮箱 */
    private Email email;

    /** 手机号 */
    private String phone;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    /** 锁定状态：0-未锁定，1-已锁定 */
    private Integer lockStatus;

    /** 登录失败次数 */
    private Integer failCount;

    /** 锁定时间 */
    private LocalDateTime lockTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 无参构造函数，设置默认值
     */
    public User() {
        this.status = 1;
        this.lockStatus = 0;
        this.failCount = 0;
    }

    /**
     * 创建用户（工厂方法）
     * @param username 用户名
     * @param password 已加密的密码
     * @param email 邮箱值对象
     * @return 用户实体
     */
    public static User create(String username, String password, Email email) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.email = email;
        user.status = 1;
        user.lockStatus = 0;
        user.failCount = 0;

        // 添加领域事件
        user.addDomainEvent(new UserCreatedEvent(
                user.getId() != null ? String.valueOf(user.getId()) : null,
                username,
                email.getValue(),
                username
        ));

        return user;
    }

    /**
     * 验证密码（使用 BCrypt）
     * @param rawPassword 明文密码
     * @return 是否匹配
     */
    public boolean matchPassword(String rawPassword) {
        if (rawPassword == null || this.password == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, this.password);
    }

    /**
     * 设置加密密码
     * @param rawPassword 明文密码
     */
    public void setEncodedPassword(String rawPassword) {
        this.password = passwordEncoder.encode(rawPassword);
    }

    /**
     * 激活用户
     */
    public void activate() {
        if (this.status == 1) {
            throw new IllegalStateException("用户已是激活状态");
        }
        this.status = 1;

        addDomainEvent(new UserActivatedEvent(String.valueOf(this.id), this.username));
    }

    /**
     * 禁用用户
     */
    public void deactivate() {
        if (this.status == 0) {
            throw new IllegalStateException("用户已是禁用状态");
        }
        this.status = 0;

        addDomainEvent(new UserDeactivatedEvent(String.valueOf(this.id), this.username, "管理员操作"));
    }

    /**
     * 更新邮箱
     * @param newEmail 新邮箱
     */
    public void updateEmail(Email newEmail) {
        if (newEmail == null) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        String oldEmailValue = this.email != null ? this.email.getValue() : null;
        this.email = newEmail;

        addDomainEvent(new UserEmailUpdatedEvent(
                String.valueOf(this.id), oldEmailValue, newEmail.getValue()));
    }

    /**
     * 修改密码
     * @param newPassword 新密码（明文）
     */
    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("密码长度不能小于6位");
        }
        this.password = passwordEncoder.encode(newPassword);

        addDomainEvent(new UserPasswordChangedEvent(String.valueOf(this.id), this.username));
    }

    /**
     * 锁定用户
     */
    public void lock() {
        if (this.lockStatus == 1) {
            return;
        }
        this.lockStatus = 1;
        this.lockTime = LocalDateTime.now();
    }

    /**
     * 解锁用户
     */
    public void unlock() {
        if (this.lockStatus == 0) {
            return;
        }
        this.lockStatus = 0;
        this.failCount = 0;
        this.lockTime = null;
    }

    /**
     * 增加登录失败次数
     */
    public void incrementFailCount() {
        if (this.failCount == null) {
            this.failCount = 0;
        }
        this.failCount++;
        if (this.failCount >= 5) {
            lock();
        }
    }

    /**
     * 重置登录失败次数
     */
    public void resetFailCount() {
        this.failCount = 0;
    }

    /**
     * 获取剩余尝试次数
     * @return 剩余次数
     */
    public int getRemainingAttempts() {
        if (this.failCount == null) {
            return 5;
        }
        return Math.max(0, 5 - this.failCount);
    }

    /**
     * 判断用户是否激活
     * @return 是否激活
     */
    public boolean isActive() {
        return this.status != null && this.status == 1;
    }

    /**
     * 判断用户是否锁定
     * @return 是否锁定
     */
    public boolean isLocked() {
        return this.lockStatus != null && this.lockStatus == 1;
    }

    /**
     * 设置ID（同时更新事件中的聚合根ID）
     * @param id 用户ID
     */
    public void setIdWithEvent(Long id) {
        this.id = id;
        // 可以在这里更新已有事件中的aggregateId
    }

    /**
     * 获取邮箱字符串
     * @return 邮箱字符串
     */
    public String getEmailValue() {
        return this.email != null ? this.email.getValue() : null;
    }

    /**
     * 获取失败次数（安全获取）
     * @return 失败次数
     */
    public Integer getFailCount() {
        return failCount != null ? failCount : 0;
    }

    /**
     * 获取锁定状态（安全获取）
     * @return 锁定状态
     */
    public Integer getLockStatus() {
        return lockStatus != null ? lockStatus : 0;
    }

    /**
     * 获取状态（安全获取）
     * @return 状态
     */
    public Integer getStatus() {
        return status != null ? status : 1;
    }
}