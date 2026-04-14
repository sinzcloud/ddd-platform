package com.ddd.platform.infrastructure.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDistributedLock implements DistributedLock {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_PREFIX = "lock:";
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "   return redis.call('del', KEYS[1]) " +
                    "else " +
                    "   return 0 " +
                    "end";

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        String lockKey = LOCK_PREFIX + key;
        String requestId = Thread.currentThread().getId() + "-" + System.nanoTime();

        long startTime = System.currentTimeMillis();
        long timeout = unit.toMillis(waitTime);

        try {
            while (System.currentTimeMillis() - startTime < timeout) {
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(lockKey, requestId, leaseTime, unit);
                if (Boolean.TRUE.equals(success)) {
                    log.debug("获取锁成功: key={}, requestId={}", key, requestId);
                    return true;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断: key={}", key, e);
        }

        log.warn("获取锁超时: key={}", key);
        return false;
    }

    @Override
    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        String requestId = Thread.currentThread().getId() + "-" + System.nanoTime();

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(UNLOCK_SCRIPT);
        script.setResultType(Long.class);

        Long result = redisTemplate.execute(script, Collections.singletonList(lockKey), requestId);
        if (result != null && result > 0) {
            log.debug("释放锁成功: key={}", key);
        }
    }

    @Override
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit unit, LockCallback<T> callback) {
        boolean locked = tryLock(key, waitTime, leaseTime, unit);
        if (!locked) {
            throw new RuntimeException("获取锁失败: " + key);
        }

        try {
            return callback.execute();
        } finally {
            unlock(key);
        }
    }
}