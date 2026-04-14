package com.ddd.platform.domain.user.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Email值对象测试")
class EmailTest {

    @Test
    @DisplayName("创建有效邮箱应该成功")
    void shouldCreateValidEmail() {
        Email email = new Email("test@example.com");
        assertEquals("test@example.com", email.getValue());
        assertEquals("example.com", email.getDomain());
        assertEquals("test", email.getUsername());
    }

    @Test
    @DisplayName("创建无效邮箱应该抛出异常")
    void shouldThrowExceptionForInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid"));
        assertThrows(IllegalArgumentException.class, () -> new Email(""));
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
        assertThrows(IllegalArgumentException.class, () -> new Email("test@"));
        assertThrows(IllegalArgumentException.class, () -> new Email("@example.com"));
    }

    @Test
    @DisplayName("相同邮箱应该相等")
    void shouldBeEqualWhenSameValue() {
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    @DisplayName("不同邮箱应该不相等")
    void shouldNotBeEqualWhenDifferentValue() {
        Email email1 = new Email("test1@example.com");
        Email email2 = new Email("test2@example.com");
        assertNotEquals(email1, email2);
    }
}