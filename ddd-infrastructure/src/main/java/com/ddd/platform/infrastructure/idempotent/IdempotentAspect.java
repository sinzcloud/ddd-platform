package com.ddd.platform.infrastructure.idempotent;

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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

    private final RedisService redisService;
    private final SpelExpressionParser spelParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private static final String IDEMPOTENT_PREFIX = "idempotent:";

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String key = buildKey(idempotent, joinPoint);
        String value = UUID.randomUUID().toString();

        // 尝试设置幂等key
        Boolean success = redisService.setIfAbsent(key, value, idempotent.timeout(), idempotent.timeUnit());

        if (Boolean.FALSE.equals(success)) {
            log.warn("重复请求被拦截: key={}", key);
            throw new BizException(idempotent.message());
        }

        try {
            return joinPoint.proceed();
        } finally {
            // 注意：如果是异步操作，不能立即删除，需要根据业务决定
            // redisService.delete(key);
        }
    }

    private String buildKey(Idempotent idempotent, ProceedingJoinPoint joinPoint) {
        String key = idempotent.key();
        if (key.isEmpty()) {
            // 默认使用：Token + URL + 参数
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization");
            String url = request.getRequestURI();
            return IDEMPOTENT_PREFIX + token + ":" + url;
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
        return IDEMPOTENT_PREFIX + parsedKey;
    }
}