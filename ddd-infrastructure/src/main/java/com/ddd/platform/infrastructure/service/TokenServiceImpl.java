package com.ddd.platform.infrastructure.service;

import com.ddd.platform.common.constant.Constants;
import com.ddd.platform.common.enums.ErrorCode;
import com.ddd.platform.common.exception.BizException;
import com.ddd.platform.infrastructure.redis.RedisService;
import com.ddd.platform.infrastructure.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtUtils jwtUtils;
    private final RedisService redisService;

    private static final long TOKEN_EXPIRE_SECONDS = 24 * 60 * 60L;

    @Override
    public String generateToken(String username, Long userId) {
        log.debug("生成Token: username={}, userId={}", username, userId);

        String token = jwtUtils.generateToken(username, userId);

        // 存储到Redis
        String redisKey = Constants.REDIS_TOKEN_KEY + username;
        redisService.set(redisKey, token, TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        return token;
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        // 去除Bearer前缀
        if (token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.substring(7);
        }

        // 验证JWT
        if (!jwtUtils.validateToken(token)) {
            log.debug("Token验证失败: JWT无效");
            return false;
        }

        // 验证Redis中是否存在
        String username = jwtUtils.extractUsername(token);
        if (username == null) {
            return false;
        }

        String redisKey = Constants.REDIS_TOKEN_KEY + username;
        Object cachedToken = redisService.get(redisKey);

        boolean isValid = cachedToken != null && cachedToken.equals(token);
        if (!isValid) {
            log.debug("Token验证失败: Redis中不存在");
        }

        return isValid;
    }

    @Override
    public String extractUsername(String token) {
        if (token == null) {
            return null;
        }

        if (token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.substring(7);
        }

        return jwtUtils.extractUsername(token);
    }

    @Override
    public Long extractUserId(String token) {
        if (token == null) {
            return null;
        }

        if (token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.substring(7);
        }

        return jwtUtils.extractUserId(token);
    }

    @Override
    public void invalidateToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }

        if (token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.substring(7);
        }

        String username = jwtUtils.extractUsername(token);
        if (username != null) {
            String redisKey = Constants.REDIS_TOKEN_KEY + username;
            redisService.delete(redisKey);
            log.info("Token已失效: username={}", username);
        }
    }

    @Override
    public String refreshToken(String oldToken) {
        if (oldToken == null || oldToken.isEmpty()) {
            throw new BizException(ErrorCode.TOKEN_INVALID, "Token不能为空");
        }

        if (oldToken.startsWith(Constants.TOKEN_PREFIX)) {
            oldToken = oldToken.substring(7);
        }

        // 验证旧Token
        if (!jwtUtils.validateToken(oldToken)) {
            throw new BizException(ErrorCode.TOKEN_EXPIRED, "Token已过期，请重新登录");
        }

        // 获取用户信息
        String username = jwtUtils.extractUsername(oldToken);
        Long userId = jwtUtils.extractUserId(oldToken);

        if (username == null || userId == null) {
            throw new BizException(ErrorCode.TOKEN_INVALID, "Token无效");
        }

        // 验证Redis中的Token
        String redisKey = Constants.REDIS_TOKEN_KEY + username;
        Object cachedToken = redisService.get(redisKey);

        if (cachedToken == null || !cachedToken.equals(oldToken)) {
            throw new BizException(ErrorCode.TOKEN_INVALID, "Token已失效，请重新登录");
        }

        // 生成新Token
        String newToken = jwtUtils.generateToken(username, userId);

        // 更新Redis
        redisService.set(redisKey, newToken, TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        log.info("刷新Token成功: username={}", username);
        return newToken;
    }
}