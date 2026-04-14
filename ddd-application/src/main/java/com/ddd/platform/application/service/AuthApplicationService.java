package com.ddd.platform.application.service;

import com.ddd.platform.common.enums.ErrorCode;
import com.ddd.platform.common.exception.BizException;
import com.ddd.platform.domain.metrics.MetricsPort;
import com.ddd.platform.domain.user.entity.User;
import com.ddd.platform.domain.user.repository.UserRepository;
import com.ddd.platform.domain.user.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final UserDomainService userDomainService;
    private final UserRepository userRepository;

    /**
     * 验证用户凭证（登录验证）
     * @param username 用户名
     * @param password 明文密码
     * @return 验证通过的用户
     */
    public User validateCredentials(String username, String password) {
        log.info("验证用户凭证: username={}", username);
        return userDomainService.validateCredentials(username, password);
    }

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public User getUserInfo(Long userId) {
        log.debug("获取用户信息: userId={}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    public User getUserByUsername(String username) {
        log.debug("根据用户名获取用户: username={}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 检查用户是否存在
     * @param username 用户名
     * @return 是否存在
     */
    public boolean checkUserExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 获取用户ID
     * @param username 用户名
     * @return 用户ID
     */
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 验证用户状态（是否可用）
     * @param userId 用户ID
     * @return 是否可用
     */
    public boolean isUserActive(Long userId) {
        return userRepository.findById(userId)
                .map(User::isActive)
                .orElse(false);
    }
}