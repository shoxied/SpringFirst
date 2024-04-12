package org.example.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DetailRestAspect {
    private static final DecimalFormat df = new DecimalFormat("#.###");

    @Around("@within(org.springframework.web.bind.annotation.RestController) || @annotation(org.springframework.web.bind.annotation.RestController)")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable{

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = StringUtils.substringAfterLast(signature.getDeclaringTypeName(), ".");
        String methodName = signature.getName();

        log.debug("invoke {}#{}", className, methodName);

        long timer = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.debug("result at {}s {}#{}", at(timer), className, methodName);
            return result;
        } catch (Throwable e) {
            log.warn("error at {}s {}#{}: {}", at(timer), className, methodName, e.getMessage());
            throw e;
        }
    }

    private String at(long timer) {
        long now = System.currentTimeMillis();
        float d = now - timer;
        return df.format(d / 1000.);
    }
}
