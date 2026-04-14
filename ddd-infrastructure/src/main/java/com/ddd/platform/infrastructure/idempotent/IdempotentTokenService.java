package com.ddd.platform.infrastructure.idempotent;

import com.ddd.platform.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotentTokenService {

    private final RedisService redisService;

    private static final String TOKEN_PREFIX = "idempotent:token:";
    private static final int TOKEN_EXPIRE_SECONDS = 60;

    // Lua脚本，保证原子性：检查并删除Token
    private static final String CHECK_AND_DELETE_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "   return redis.call('del', KEYS[1]) " +
                    "else " +
                    "   return 0 " +
                    "end";

    /**
     * 生成幂等Token
     */
    public String generateToken() {
        String token = UUID.randomUUID().toString();
        String key = TOKEN_PREFIX + token;
        redisService.set(key, "1", TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
        log.debug("生成幂等Token: {}", token);
        return token;
    }

    /**
     * 生成带业务标识的幂等Token
     */
    public String generateToken(String businessKey) {
        String token = UUID.randomUUID().toString();
        String key = TOKEN_PREFIX + businessKey + ":" + token;
        redisService.set(key, businessKey, TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
        log.debug("生成幂等Token: businessKey={}, token={}", businessKey, token);
        return token;
    }

    /**
     * 校验并删除Token（普通方式）
     */
    public boolean checkAndDeleteToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        String key = TOKEN_PREFIX + token;
        if (Boolean.TRUE.equals(redisService.hasKey(key))) {
            redisService.delete(key);
            log.debug("校验并删除Token成功: {}", token);
            return true;
        }
        log.warn("Token不存在或已使用: {}", token);
        return false;
    }

    /**
     * 校验并删除Token（Lua脚本方式，原子操作）
     */
    public boolean checkAndDeleteTokenAtomic(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        String key = TOKEN_PREFIX + token;
        RedisScript<Long> script = new DefaultRedisScript<>(CHECK_AND_DELETE_SCRIPT, Long.class);
        Long result = redisService.executeScript(script, Collections.singletonList(key), "1");

        boolean success = result != null && result > 0;
        if (success) {
            log.debug("原子操作校验并删除Token成功: {}", token);
        } else {
            log.warn("原子操作：Token不存在或已使用: {}", token);
        }
        return success;
    }

    /**
     * 校验Token是否存在（不删除）
     */
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        String key = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisService.hasKey(key));
    }

    /**
     * 手动删除Token
     */
    public void deleteToken(String token) {
        if (token != null && !token.isEmpty()) {
            String key = TOKEN_PREFIX + token;
            redisService.delete(key);
            log.debug("手动删除Token: {}", token);
        }
    }
}