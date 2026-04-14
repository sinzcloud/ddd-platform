package com.ddd.platform.domain.user.entity;

import com.ddd.platform.domain.user.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("用户实体测试")
class UserTest {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private User user;

    @BeforeEach
    void setUp() {
        Email email = new Email("test@example.com");
        // 使用加密后的密码创建用户
        String encodedPassword = passwordEncoder.encode("123456");
        user = User.create("testuser", encodedPassword, email);
        user.setId(1L);
    }

    @Test
    @DisplayName("创建用户应该成功")
    void shouldCreateUserSuccessfully() {
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertTrue(user.isActive());
        assertFalse(user.isLocked());
        assertEquals(0, user.getFailCount());
        assertNotNull(user.getEmail());
        assertEquals("test@example.com", user.getEmail().getValue());
    }

    @Test
    @DisplayName("激活用户应该成功")
    void shouldActivateUser() {
        // 先禁用用户
        user.deactivate();
        assertFalse(user.isActive());

        // 再激活
        user.activate();
        assertTrue(user.isActive());
    }

    @Test
    @DisplayName("禁用用户应该成功")
    void shouldDeactivateUser() {
        // 用户默认是激活状态，直接禁用
        user.deactivate();
        assertFalse(user.isActive());
    }

    @Test
    @DisplayName("激活已经是激活状态的用户应该抛出异常")
    void shouldThrowExceptionWhenActivatingActiveUser() {
        // 用户默认是激活状态
        assertThrows(IllegalStateException.class, () -> user.activate());
    }

    @Test
    @DisplayName("禁用已经是禁用状态的用户应该抛出异常")
    void shouldThrowExceptionWhenDeactivatingInactiveUser() {
        user.deactivate();
        assertThrows(IllegalStateException.class, () -> user.deactivate());
    }

    @Test
    @DisplayName("增加失败次数应该正确")
    void shouldIncrementFailCount() {
        user.incrementFailCount();
        assertEquals(1, user.getFailCount());

        user.incrementFailCount();
        assertEquals(2, user.getFailCount());
    }

    @Test
    @DisplayName("失败5次后应该锁定用户")
    void shouldLockUserAfter5Failures() {
        for (int i = 0; i < 5; i++) {
            user.incrementFailCount();
        }

        assertTrue(user.isLocked());
        assertEquals(5, user.getFailCount());
    }

    @Test
    @DisplayName("重置失败次数应该成功")
    void shouldResetFailCount() {
        user.incrementFailCount();
        user.incrementFailCount();
        assertEquals(2, user.getFailCount());

        user.resetFailCount();
        assertEquals(0, user.getFailCount());
    }

    @Test
    @DisplayName("解锁用户应该成功")
    void shouldUnlockUser() {
        // 先失败5次锁定用户
        for (int i = 0; i < 5; i++) {
            user.incrementFailCount();
        }
        assertTrue(user.isLocked());

        user.unlock();
        assertFalse(user.isLocked());
        assertEquals(0, user.getFailCount());
    }

    @Test
    @DisplayName("更新邮箱应该成功")
    void shouldUpdateEmail() {
        Email newEmail = new Email("newemail@example.com");
        user.updateEmail(newEmail);
        assertEquals("newemail@example.com", user.getEmail().getValue());
    }

    @Test
    @DisplayName("修改密码应该成功")
    void shouldChangePassword() {
        String oldPassword = user.getPassword();
        user.changePassword("654321");
        assertNotEquals(oldPassword, user.getPassword());
    }

    @Test
    @DisplayName("验证正确密码应该返回true")
    void shouldMatchCorrectPassword() {
        // 注意：这里需要用加密后的密码
        // 实际密码是 "123456" 的 BCrypt 加密结果
        assertTrue(user.matchPassword("123456"));
    }

    @Test
    @DisplayName("验证错误密码应该返回false")
    void shouldNotMatchWrongPassword() {
        assertFalse(user.matchPassword("wrongpassword"));
    }

    @Test
    @DisplayName("获取剩余尝试次数应该正确")
    void shouldGetRemainingAttempts() {
        assertEquals(5, user.getRemainingAttempts());

        user.incrementFailCount();
        assertEquals(4, user.getRemainingAttempts());

        user.incrementFailCount();
        assertEquals(3, user.getRemainingAttempts());
    }
}