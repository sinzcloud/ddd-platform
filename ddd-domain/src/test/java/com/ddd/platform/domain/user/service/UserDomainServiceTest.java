package com.ddd.platform.domain.user.service;

import com.ddd.platform.common.exception.BizException;
import com.ddd.platform.domain.metrics.MetricsPort;
import com.ddd.platform.domain.user.aggregate.UserAggregate;
import com.ddd.platform.domain.user.entity.User;
import com.ddd.platform.domain.user.repository.UserProfileRepository;
import com.ddd.platform.domain.user.repository.UserRepository;
import com.ddd.platform.domain.user.valueobject.Email;
import com.ddd.platform.domain.user.valueobject.UserId;
import com.ddd.platform.domain.user.valueobject.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户领域服务测试")
class UserDomainServiceTest {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private MetricsPort metricsPort;  // 添加 Mock

    @InjectMocks
    private UserDomainServiceImpl userDomainService;

    private User user;
    private UserProfile profile;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        // 使用 BCrypt 加密密码
        encodedPassword = passwordEncoder.encode("123456");

        Email email = new Email("test@example.com");
        user = User.create("testuser", encodedPassword, email);
        user.setId(1L);

        profile = UserProfile.create(1L, "测试用户");
        profile.setId(1L);
    }

    @Test
    @DisplayName("创建用户应该成功")
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);

        UserAggregate aggregate = userDomainService.createUser(
                "testuser", encodedPassword, "test@example.com", "测试用户");

        assertNotNull(aggregate);
        assertEquals(1L, aggregate.getUserId());
        assertEquals("testuser", aggregate.getUsername());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("创建用户时用户名重复应该抛出异常")
    void shouldThrowExceptionWhenUsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(BizException.class, () -> {
            userDomainService.createUser("testuser", encodedPassword, "test@example.com", "测试用户");
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("激活用户应该成功")
    void shouldActivateUserSuccessfully() {
        user.deactivate();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User activatedUser = userDomainService.activateUser(new UserId(1L));

        assertTrue(activatedUser.isActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("激活不存在的用户应该抛出异常")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BizException.class, () -> {
            userDomainService.activateUser(new UserId(999L));
        });
    }

    @Test
    @DisplayName("验证正确凭证应该成功")
    void shouldValidateCorrectCredentials() {
        // 关键：mock matchPassword 返回 true
        // 使用 spy 或者修改 User 的 matchPassword 行为
        User spyUser = spy(user);
        when(spyUser.matchPassword("123456")).thenReturn(true);
        when(spyUser.isActive()).thenReturn(true);
        when(spyUser.isLocked()).thenReturn(false);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(spyUser));
        when(userRepository.save(any(User.class))).thenReturn(spyUser);

        User validatedUser = userDomainService.validateCredentials("testuser", "123456");

        assertNotNull(validatedUser);
        assertEquals("testuser", validatedUser.getUsername());

        // 验证 resetFailCount 被调用
        verify(spyUser, times(1)).resetFailCount();
        verify(userRepository, times(1)).save(spyUser);
    }

    @Test
    @DisplayName("验证错误凭证应该抛出异常")
    void shouldThrowExceptionForWrongPassword() {
        User spyUser = spy(user);
        when(spyUser.matchPassword("wrongpassword")).thenReturn(false);
        when(spyUser.isActive()).thenReturn(true);
        when(spyUser.isLocked()).thenReturn(false);
        when(spyUser.getFailCount()).thenReturn(0);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(spyUser));
        when(userRepository.save(any(User.class))).thenReturn(spyUser);

        assertThrows(BizException.class, () -> {
            userDomainService.validateCredentials("testuser", "wrongpassword");
        });

        // 验证 incrementFailCount 被调用
        verify(spyUser, times(1)).incrementFailCount();
        verify(userRepository, times(1)).save(spyUser);
    }
}