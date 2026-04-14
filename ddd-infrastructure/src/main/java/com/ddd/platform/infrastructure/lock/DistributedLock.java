package com.ddd.platform.infrastructure.lock;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {

    /**
     * 尝试获取锁
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     */
    void unlock(String key);

    /**
     * 执行带锁的业务逻辑
     */
    <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit unit, LockCallback<T> callback);

    @FunctionalInterface
    interface LockCallback<T> {
        T execute();
    }
}