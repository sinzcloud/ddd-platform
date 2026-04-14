package com.ddd.platform.domain.valueobject;

import java.io.Serializable;

/**
 * 值对象基类
 */
public abstract class BaseValueObject implements Serializable {

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}