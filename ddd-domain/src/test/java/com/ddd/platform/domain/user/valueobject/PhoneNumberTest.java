package com.ddd.platform.domain.user.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("手机号值对象测试")
class PhoneNumberTest {

    @Test
    @DisplayName("创建有效手机号应该成功")
    void shouldCreateValidPhoneNumber() {
        PhoneNumber phone = new PhoneNumber("13800138000");
        assertEquals("13800138000", phone.getValue());
        assertEquals("138****8000", phone.getMasked());
        assertEquals("+8613800138000", phone.getFullNumber());
    }

    @Test
    @DisplayName("创建无效手机号应该抛出异常")
    void shouldThrowExceptionForInvalidPhone() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("123"));
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("138001380001"));
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber(""));
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber(null));
    }
}