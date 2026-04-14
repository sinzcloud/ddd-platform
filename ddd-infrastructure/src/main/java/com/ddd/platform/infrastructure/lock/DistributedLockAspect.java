package com.ddd.platform.infrastructure.lock;

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

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final DistributedLock distributedLock;
    private final SpelExpressionParser spelParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(distributedLockAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLockAnnotation distributedLockAnnotation) throws Throwable {
        // 解析锁的key
        String key = parseKey(distributedLockAnnotation.key(), joinPoint);

        long waitTime = distributedLockAnnotation.waitTime();
        long leaseTime = distributedLockAnnotation.leaseTime();
        TimeUnit timeUnit = distributedLockAnnotation.timeUnit();

        // 尝试获取锁并执行
        boolean locked = distributedLock.tryLock(key, waitTime, leaseTime, timeUnit);

        if (!locked) {
            log.warn("获取分布式锁失败: key={}", key);
            throw new RuntimeException("系统繁忙，请稍后再试");
        }

        try {
            return joinPoint.proceed();
        } finally {
            distributedLock.unlock(key);
            log.debug("释放分布式锁: key={}", key);
        }
    }

    /**
     * 解析SpEL表达式，生成锁的key
     */
    private String parseKey(String keySpel, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);

        StandardEvaluationContext context = new StandardEvaluationContext();

        // 设置方法参数
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        // 设置额外变量
        context.setVariable("method", method.getName());
        context.setVariable("target", joinPoint.getTarget());

        // 解析表达式
        try {
            return spelParser.parseExpression(keySpel).getValue(context, String.class);
        } catch (Exception e) {
            log.error("解析锁key失败: {}", keySpel, e);
            return keySpel;
        }
    }
}