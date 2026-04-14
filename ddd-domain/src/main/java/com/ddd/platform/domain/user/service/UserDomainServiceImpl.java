package com.ddd.platform.domain.user.service;

import com.ddd.platform.common.enums.ErrorCode;
import com.ddd.platform.common.exception.BizException;
import com.ddd.platform.domain.metrics.MetricsPort;
import com.ddd.platform.domain.user.aggregate.UserAggregate;
import com.ddd.platform.domain.user.entity.User;
import com.ddd.platform.domain.user.repository.UserProfileRepository;
import com.ddd.platform.domain.user.repository.UserRepository;
import com.ddd.platform.domain.user.valueobject.Email;
import com.ddd.platform.domain.user.valueobject.UserId;
import com.ddd.platform.domain.user.valueobject.UserProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户领域服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final MetricsPort metricsPort;

    @Override
    @Transactional
    public UserAggregate createUser(String username, String password, String email, String nickname) {
        log.info("创建用户: username={}, email={}", username, email);

        // 1. 校验用户名是否已存在
        if (isUsernameExists(username)) {
            log.warn("用户名已存在: username={}", username);
            throw new BizException(ErrorCode.USERNAME_EXIST);
        }

        // 2. 校验邮箱格式和是否已存在
        Email emailVO;
        try {
            emailVO = new Email(email);
        } catch (IllegalArgumentException e) {
            log.warn("邮箱格式错误: email={}", email);
            throw new BizException(ErrorCode.BAD_REQUEST, "邮箱格式不正确");
        }

        if (isEmailExists(emailVO)) {
            log.warn("邮箱已被使用: email={}", email);
            throw new BizException(ErrorCode.USERNAME_EXIST, "邮箱已被使用");
        }

        // 3. 创建用户实体
        User user = User.create(username, password, emailVO);
        user = userRepository.save(user);

        // 4. 创建用户资料
        String finalNickname = (nickname != null && !nickname.isEmpty()) ? nickname : username;
        UserProfile profile = UserProfile.create(user.getId(), finalNickname);
        profile = userProfileRepository.save(profile);

        log.info("用户创建成功: userId={}, username={}", user.getId(), username);
        return new UserAggregate(user, profile);
    }

    @Override
    @Transactional
    public User activateUser(UserId userId) {
        log.info("激活用户: userId={}", userId.getValue());

        User user = getUser(userId)
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", userId.getValue());
                    return new BizException(ErrorCode.USER_NOT_FOUND);
                });

        if (user.isActive()) {
            log.warn("用户已是激活状态: userId={}", userId.getValue());
            throw new BizException(ErrorCode.BAD_REQUEST, "用户已是激活状态");
        }

        user.activate();
        User savedUser = userRepository.save(user);
        log.info("用户激活成功: userId={}, username={}", savedUser.getId(), savedUser.getUsername());

        return savedUser;
    }

    @Override
    @Transactional
    public User deactivateUser(UserId userId) {
        log.info("禁用用户: userId={}", userId.getValue());

        User user = getUser(userId)
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", userId.getValue());
                    return new BizException(ErrorCode.USER_NOT_FOUND);
                });

        if (!user.isActive()) {
            log.warn("用户已是禁用状态: userId={}", userId.getValue());
            throw new BizException(ErrorCode.BAD_REQUEST, "用户已是禁用状态");
        }

        user.deactivate();
        User savedUser = userRepository.save(user);
        log.info("用户禁用成功: userId={}, username={}", savedUser.getId(), savedUser.getUsername());

        return savedUser;
    }

    @Override
    @Transactional
    public User updateEmail(UserId userId, Email newEmail) {
        log.info("更新邮箱: userId={}, newEmail={}", userId.getValue(), newEmail.getValue());

        User user = getUser(userId)
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", userId.getValue());
                    return new BizException(ErrorCode.USER_NOT_FOUND);
                });

        if (isEmailExists(newEmail)) {
            log.warn("邮箱已被使用: email={}", newEmail.getValue());
            throw new BizException(ErrorCode.USERNAME_EXIST, "邮箱已被使用");
        }

        user.updateEmail(newEmail);
        User savedUser = userRepository.save(user);
        log.info("邮箱更新成功: userId={}, newEmail={}", savedUser.getId(), newEmail.getValue());

        return savedUser;
    }

    /**
     * 独立事务：增加失败次数（不受主事务回滚影响）
     */
    public void incrementFailCount(User user) {
        user.incrementFailCount();
        userRepository.save(user);
        log.info("失败次数已保存: userId={}, failCount={}", user.getId(), user.getFailCount());
    }

    @Override
    @Transactional
    public User validateCredentials(String username, String password) {
        log.debug("验证用户凭证: username={}", username);

        // 1. 查询用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("用户不存在: username={}", username);
                    return new BizException(ErrorCode.USER_NOT_FOUND);
                });

        // 2. 检查账号是否被禁用
        if (!user.isActive()) {
            log.warn("账号已被禁用: username={}", username);
            throw new BizException(ErrorCode.UNAUTHORIZED, "账号已被禁用");
        }

        // 3. 检查账号是否被锁定
        if (user.isLocked()) {
            log.warn("账号已被锁定: username={}", username);
            throw new BizException(ErrorCode.UNAUTHORIZED, "账号已被锁定，请30分钟后重试");
        }

        // 4. 验证密码
        if (!user.matchPassword(password)) {
            // 增加失败次数
            incrementFailCount(user);

            int remainingAttempts = user.getRemainingAttempts();
            log.warn("密码错误: username={}, 失败次数={}, 剩余次数={}",
                    username, user.getFailCount(), remainingAttempts);

            // 记录登录失败
            metricsPort.recordLoginFailure();

            // 检查是否应该锁定
            if (user.isLocked()) {
                throw new BizException(ErrorCode.UNAUTHORIZED, "密码错误次数过多，账号已被锁定");
            }

            throw new BizException(ErrorCode.PASSWORD_ERROR,
                    String.format("密码错误，还剩%d次尝试机会", remainingAttempts));
        }

        // 5. 登录成功，重置失败次数
        user.resetFailCount();
        userRepository.save(user);

        // 记录登录成功
        metricsPort.recordUserLogin();

        log.info("用户登录成功: username={}", username);
        return user;
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailExists(Email email) {
        return userRepository.existsByEmail(email.getValue());
    }

    @Override
    public Optional<User> getUser(UserId userId) {
        return userRepository.findById(userId.getValue());
    }

    @Override
    @Transactional
    public void lockUser(UserId userId) {
        log.info("锁定用户: userId={}", userId.getValue());

        User user = getUser(userId)
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", userId.getValue());
                    return new BizException(ErrorCode.USER_NOT_FOUND);
                });

        if (user.isLocked()) {
            log.warn("用户已是锁定状态: userId={}", userId.getValue());
            throw new BizException(ErrorCode.BAD_REQUEST, "用户已是锁定状态");
        }

        user.lock();
        userRepository.save(user);
        log.info("用户锁定成功: userId={}", userId.getValue());
    }

    @Override
    @Transactional
    public void unlockUser(UserId userId) {
        log.info("解锁用户: userId={}", userId.getValue());

        User user = getUser(userId)
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", userId.getValue());
                    return new BizException(ErrorCode.USER_NOT_FOUND);
                });

        if (!user.isLocked()) {
            log.warn("用户未锁定: userId={}", userId.getValue());
            throw new BizException(ErrorCode.BAD_REQUEST, "用户未锁定");
        }

        user.unlock();
        userRepository.save(user);
        log.info("用户解锁成功: userId={}", userId.getValue());
    }
}