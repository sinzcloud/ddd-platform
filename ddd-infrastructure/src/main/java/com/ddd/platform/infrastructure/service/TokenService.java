package com.ddd.platform.infrastructure.service;

/**
 * Token服务接口（定义在application层，实现在infrastructure层）
 * 用于打破循环依赖
 */
public interface TokenService {

    /**
     * 生成Token
     * @param username 用户名
     * @param userId 用户ID
     * @return Token字符串
     */
    String generateToken(String username, Long userId);

    /**
     * 验证Token是否有效
     * @param token Token字符串
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 从Token中提取用户名
     * @param token Token字符串
     * @return 用户名
     */
    String extractUsername(String token);

    /**
     * 从Token中提取用户ID
     * @param token Token字符串
     * @return 用户ID
     */
    Long extractUserId(String token);

    /**
     * 使Token失效（登出）
     * @param token Token字符串
     */
    void invalidateToken(String token);

    /**
     * 刷新Token
     * @param oldToken 旧Token
     * @return 新Token
     */
    String refreshToken(String oldToken);
}