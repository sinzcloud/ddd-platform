package com.ddd.platform.infrastructure.ratelimit;

import com.ddd.platform.common.exception.BizException;
import com.ddd.platform.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect {

    private final RedisService redisService;
    private final SpelExpressionParser spelParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private static final String RATE_LIMITER_PREFIX = "ratelimit:";

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        String key = buildKey(rateLimiter, joinPoint);
        boolean allowed = tryAcquire(key, rateLimiter.limit(), rateLimiter.duration());

        if (!allowed) {
            log.warn("请求被限流: key={}, limit={}/{}s", key, rateLimiter.limit(), rateLimiter.duration());
            throw new BizException(rateLimiter.message());
        }

        return joinPoint.proceed();
    }

    /**
     * 滑动窗口限流算法
     */
    private boolean tryAcquire(String key, int limit, int duration) {
        String windowKey = RATE_LIMITER_PREFIX + key;
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - TimeUnit.SECONDS.toMillis(duration);

        // 移除窗口外的记录
        redisService.removeRangeByScore(windowKey, 0, windowStart);

        // 获取当前窗口内的请求数
        Long count = redisService.countByScore(windowKey, windowStart, currentTime);

        if (count != null && count >= limit) {
            return false;
        }

        // 添加当前请求
        redisService.addToSortedSet(windowKey, String.valueOf(currentTime), currentTime);
        redisService.expire(windowKey, duration, TimeUnit.SECONDS);

        return true;
    }

    private String buildKey(RateLimiter rateLimiter, ProceedingJoinPoint joinPoint) {
        String key = rateLimiter.key();
        if (key.isEmpty()) {
            // 默认：IP + 接口路径
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String ip = getClientIp(request);
            String url = request.getRequestURI();
            return ip + ":" + url;
        }

        // 解析SpEL表达式
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);

        StandardEvaluationContext context = new StandardEvaluationContext();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        // 设置额外变量
        context.setVariable("method", method.getName());
        context.setVariable("target", joinPoint.getTarget());

        String parsedKey = spelParser.parseExpression(key).getValue(context, String.class);
        return parsedKey;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.split(",")[0] : "unknown";
    }
}