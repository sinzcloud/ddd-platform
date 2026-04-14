package com.ddd.platform.domain.user.valueobject;

import com.ddd.platform.domain.valueobject.BaseValueObject;
import lombok.Getter;

@Getter
public class Password extends BaseValueObject {

    private final String value;
    private final String encryptedValue;

    public Password(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("密码长度不能小于6位");
        }
        this.value = rawPassword;
        this.encryptedValue = encrypt(rawPassword);
    }

    private String encrypt(String rawPassword) {
        // 实际加密会使用 BCrypt，这里只是占位
        return "{bcrypt}" + rawPassword;
    }

    public boolean matches(String rawPassword) {
        // 实际验证会使用 BCrypt
        return value.equals(rawPassword);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Password password = (Password) obj;
        return encryptedValue.equals(password.encryptedValue);
    }

    @Override
    public int hashCode() {
        return encryptedValue.hashCode();
    }

    @Override
    public String toString() {
        return "********";
    }
}