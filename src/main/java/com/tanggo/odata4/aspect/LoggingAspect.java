package com.tanggo.odata4.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;

    public LoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("within(@org.springframework.stereotype.Component *)")
    public void componentPointcut() {
    }

    @Around("componentPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = UUID.randomUUID().toString();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        // 获取请求信息
        HttpServletRequest request = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
            }
        } catch (Exception e) {
            // 忽略非Web请求的场景
        }

        // 记录请求开始
        log.info("[{}] Request started - {}.{}", requestId, className, methodName);
        if (request != null) {
            log.info("[{}] HTTP Method: {}, URL: {}", requestId, request.getMethod(), request.getRequestURL());
        }
        
        try {
            // 记录方法参数（安全处理特殊类型）
            String args = Arrays.stream(joinPoint.getArgs())
                .map(arg -> {
                    try {
                        if (arg instanceof OData || arg instanceof ServiceMetadata) {
                            return arg.getClass().getSimpleName();
                        }
                        return objectMapper.writeValueAsString(arg);
                    } catch (Exception e) {
                        return arg.toString();
                    }
                })
                .collect(Collectors.joining(", "));
            log.debug("[{}] Method arguments: [{}]", requestId, args);
            
            // 执行实际方法
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            
            // 记录执行结果
            log.info("[{}] Execution time: {}ms", requestId, executionTime);
            
            // 安全处理返回值
            if (result != null) {
                try {
                    String resultStr = objectMapper.writeValueAsString(result);
                    log.debug("[{}] Method returned: {}", requestId, resultStr);
                } catch (Exception e) {
                    log.debug("[{}] Method returned: {} (toString)", requestId, result);
                }
            } else {
                log.debug("[{}] Method returned: void", requestId);
            }
            
            return result;
        } catch (Exception e) {
            // 记录异常信息
            log.error("[{}] Exception occurred in {}.{}: {}", requestId, className, methodName, e.getMessage(), e);
            throw e;
        } finally {
            // 记录请求结束
            log.info("[{}] Request ended - {}.{}", requestId, className, methodName);
        }
    }
} 