package com.ddd.platform.domain.valueobject;

import com.ddd.platform.domain.user.valueobject.Email;
import com.ddd.platform.domain.user.valueobject.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("值对象测试")
public class ValueObjectTest {

    @Test
    @DisplayName("测试Email值对象创建")
    void testEmailCreation() {
        // 测试有效邮箱
        Email email = new Email("test@example.com");
        assertEquals("test@example.com", email.getValue());
        assertEquals("example.com", email.getDomain());
        assertEquals("test", email.getUsername());

        // 测试无效邮箱
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
        assertThrows(IllegalArgumentException.class, () -> new Email(""));
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    @DisplayName("测试Email值对象相等性")
    void testEmailEquality() {
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");
        Email email3 = new Email("test3@example.com");

        assertEquals(email1, email2);
        assertNotEquals(email1, email3);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    @DisplayName("测试PhoneNumber值对象创建")
    void testPhoneNumberCreation() {
        // 测试有效手机号
        PhoneNumber phone = new PhoneNumber("13800138000");
        assertEquals("13800138000", phone.getValue());
        assertEquals("138****8000", phone.getMasked());
        assertEquals("+8613800138000", phone.getFullNumber());

        // 测试无效手机号
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("123"));
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber(""));
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber(null));
    }

    @Test
    @DisplayName("测试PhoneNumber值对象相等性")
    void testPhoneNumberEquality() {
        PhoneNumber phone1 = new PhoneNumber("13800138000");
        PhoneNumber phone2 = new PhoneNumber("13800138000");
        PhoneNumber phone3 = new PhoneNumber("13900139000");

        assertEquals(phone1, phone2);
        assertNotEquals(phone1, phone3);
        assertEquals(phone1.hashCode(), phone2.hashCode());
    }
}