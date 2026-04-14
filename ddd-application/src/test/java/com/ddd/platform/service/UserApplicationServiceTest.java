package com.ddd.platform.application.service;

import com.ddd.platform.application.command.RegisterCommand;
import com.ddd.platform.application.dto.UserDTO;
import com.ddd.platform.common.exception.BizException;
import com.ddd.platform.domain.metrics.MetricsPort;
import com.ddd.platform.domain.user.aggregate.UserAggregate;
import com.ddd.platform.domain.user.entity.User;
import com.ddd.platform.domain.user.repository.UserRepository;
import com.ddd.platform.domain.user.service.UserDomainService;
import com.ddd.platform.domain.user.valueobject.Email;
import com.ddd.platform.domain.user.valueobject.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户应用服务测试")
class UserApplicationServiceTest {

    @Mock
    private UserDomainService userDomainService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MetricsPort metricsPort;

    @InjectMocks
    private UserApplicationService userApplicationService;

    private RegisterCommand registerCommand;
    private User user;
    private UserProfile profile;
    private UserAggregate aggregate;

    @BeforeEach
    void setUp() {
        registerCommand = new RegisterCommand();
        registerCommand.setUsername("testuser");
        registerCommand.setPassword("123456");
        registerCommand.setEmail("test@example.com");
        registerCommand.setNickname("测试用户");

        Email email = new Email("test@example.com");
        user = User.create("testuser", "encodedPassword", email);
        user.setId(1L);

        profile = UserProfile.create(1L, "测试用户");
        aggregate = new UserAggregate(user, profile);
    }

    @Test
    @DisplayName("注册用户应该成功")
    void shouldRegisterUserSuccessfully() {
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        when(userDomainService.createUser(any(), any(), any(), any())).thenReturn(aggregate);

        UserDTO result = userApplicationService.registerUser(registerCommand);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());

        verify(userDomainService, times(1)).createUser(
                "testuser", "encodedPassword", "test@example.com", "测试用户");
    }

    @Test
    @DisplayName("查询用户应该成功")
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userApplicationService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("查询不存在的用户应该抛出异常")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BizException.class, () -> {
            userApplicationService.getUserById(999L);
        });
    }
}