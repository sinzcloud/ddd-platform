package com.ddd.platform.domain.user.valueobject;

import com.ddd.platform.domain.valueobject.BaseValueObject;
import lombok.Getter;

@Getter
public class Address extends BaseValueObject {

    private final String province;
    private final String city;
    private final String district;
    private final String detail;
    private final String postalCode;

    private Address(Builder builder) {
        this.province = builder.province;
        this.city = builder.city;
        this.district = builder.district;
        this.detail = builder.detail;
        this.postalCode = builder.postalCode;
    }

    public String getFullAddress() {
        return province + city + district + detail;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Address address = (Address) obj;
        return getFullAddress().equals(address.getFullAddress());
    }

    @Override
    public int hashCode() {
        return getFullAddress().hashCode();
    }

    @Override
    public String toString() {
        return getFullAddress();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String province;
        private String city;
        private String district;
        private String detail;
        private String postalCode;

        public Builder province(String province) {
            this.province = province;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder district(String district) {
            this.district = district;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }
}