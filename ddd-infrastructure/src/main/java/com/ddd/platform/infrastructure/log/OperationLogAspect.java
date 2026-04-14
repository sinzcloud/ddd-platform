package com.ddd.platform.infrastructure.log;

import com.alibaba.fastjson2.JSON;
import com.ddd.platform.infrastructure.mapper.OperationLogMapper;
import com.ddd.platform.infrastructure.po.OperationLogPO;
import com.ddd.platform.infrastructure.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;
    private final JwtUtils jwtUtils;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 创建日志对象
        OperationLogPO logPO = new OperationLogPO();
        logPO.setModule(operationLog.module());
        logPO.setOperation(operationLog.operation());
        logPO.setType(operationLog.type());
        logPO.setCreateTime(LocalDateTime.now());

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logPO.setUrl(request.getRequestURL().toString());
            logPO.setMethod(request.getMethod());
            logPO.setIp(getClientIp(request));

            // 获取当前用户
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    String username = jwtUtils.extractUsername(token);
                    Long userId = jwtUtils.extractUserId(token);
                    logPO.setUsername(username);
                    logPO.setUserId(userId);
                } catch (Exception e) {
                    log.warn("解析Token失败: {}", e.getMessage());
                }
            }
        }

        // 保存请求参数
        if (operationLog.saveParam()) {
            Object[] args = joinPoint.getArgs();
            try {
                logPO.setRequestParams(JSON.toJSONString(args));
            } catch (Exception e) {
                logPO.setRequestParams("参数序列化失败");
            }
        }

        try {
            Object result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;

            logPO.setCostTime(costTime);
            logPO.setStatus(1);

            if (operationLog.saveResult()) {
                try {
                    logPO.setResponseData(JSON.toJSONString(result));
                } catch (Exception e) {
                    logPO.setResponseData("结果序列化失败");
                }
            }

            return result;

        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            logPO.setCostTime(costTime);
            logPO.setStatus(0);
            logPO.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 异步保存日志
            saveLogAsync(logPO);
        }
    }

    @Async
    public void saveLogAsync(OperationLogPO logPO) {
        try {
            operationLogMapper.insert(logPO);
            log.debug("操作日志保存成功: module={}, operation={}", logPO.getModule(), logPO.getOperation());
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
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