package com.ddd.platform.domain.user.valueobject;

import com.ddd.platform.domain.valueobject.BaseValueObject;
import lombok.Getter;
import java.util.regex.Pattern;

@Getter
public class PhoneNumber extends BaseValueObject {

    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private static final Pattern PATTERN = Pattern.compile(PHONE_REGEX);

    private final String value;
    private final String countryCode;

    public PhoneNumber(String value) {
        this(value, "86");
    }

    public PhoneNumber(String value, String countryCode) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (!isValid(value)) {
            throw new IllegalArgumentException("手机号格式不正确: " + value);
        }
        this.value = value;
        this.countryCode = countryCode;
    }

    private boolean isValid(String phone) {
        return PATTERN.matcher(phone).matches();
    }

    public String getFullNumber() {
        return "+" + countryCode + value;
    }

    public String getMasked() {
        return value.substring(0, 3) + "****" + value.substring(7);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PhoneNumber that = (PhoneNumber) obj;
        return value.equals(that.value) && countryCode.equals(that.countryCode);
    }

    @Override
    public int hashCode() {
        return value.hashCode() + countryCode.hashCode();
    }

    @Override
    public String toString() {
        return getMasked();
    }
}