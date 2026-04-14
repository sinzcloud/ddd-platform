package com.ddd.platform.domain.user.service;

import com.ddd.platform.domain.user.aggregate.UserAggregate;
import com.ddd.platform.domain.user.entity.User;
import com.ddd.platform.domain.user.valueobject.Email;
import com.ddd.platform.domain.user.valueobject.UserId;

import java.util.Optional;

/**
 * 用户领域服务接口
 */
public interface UserDomainService {

    /**
     * 创建用户
     * @param username 用户名
     * @param password 密码（已加密）
     * @param email 邮箱
     * @param nickname 昵称
     * @return 用户聚合根
     */
    UserAggregate createUser(String username, String password, String email, String nickname);

    /**
     * 激活用户
     * @param userId 用户ID
     * @return 激活后的用户
     */
    User activateUser(UserId userId);

    /**
     * 禁用用户
     * @param userId 用户ID
     * @return 禁用后的用户
     */
    User deactivateUser(UserId userId);

    /**
     * 更新邮箱
     * @param userId 用户ID
     * @param newEmail 新邮箱
     * @return 更新后的用户
     */
    User updateEmail(UserId userId, Email newEmail);

    /**
     * 验证用户凭证（登录）
     * @param username 用户名
     * @param password 明文密码
     * @return 验证通过的用户
     */
    User validateCredentials(String username, String password);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean isEmailExists(Email email);

    /**
     * 获取用户
     * @param userId 用户ID
     * @return 用户
     */
    Optional<User> getUser(UserId userId);

    /**
     * 锁定用户
     * @param userId 用户ID
     */
    void lockUser(UserId userId);

    /**
     * 解锁用户
     * @param userId 用户ID
     */
    void unlockUser(UserId userId);
}