package com.ddd.platform.domain.user.valueobject;

import com.ddd.platform.domain.valueobject.BaseValueObject;
import lombok.Getter;
import java.util.regex.Pattern;

@Getter
public class Email extends BaseValueObject {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

    private final String value;

    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (!isValid(value)) {
            throw new IllegalArgumentException("邮箱格式不正确: " + value);
        }
        this.value = value.toLowerCase();
    }

    private boolean isValid(String email) {
        return PATTERN.matcher(email).matches();
    }

    public String getDomain() {
        return value.substring(value.indexOf("@") + 1);
    }

    public String getUsername() {
        return value.substring(0, value.indexOf("@"));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Email email = (Email) obj;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}