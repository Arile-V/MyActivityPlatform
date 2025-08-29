package com.activity.platform.log;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;

import com.activity.platform.pojo.inner.MyLog;
import com.activity.platform.service.ILogService;
import com.activity.platform.util.IPHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAspect {
    @Resource
    private ILogService logService;

    @AfterReturning(pointcut = "@annotation(MyLog)",returning = "jsonResult")
    public void afterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint,jsonResult);
    }
    
    protected void handleLog(final JoinPoint joinPoint,Object jsonResult) {
        try{
            MyLog logs = new MyLog();
            String method = joinPoint.getTarget().getClass().getName()+
                    "."
                    +joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            String arg = Arrays.toString(args);
            String ip = IPHolder.getIp();
            logs.setMethod(method);
            logs.setIp(ip);
            logs.setRequest(arg);
            logs.setResult(jsonResult==null?"":jsonResult.toString());
            logs.setTime(Timestamp.valueOf(LocalDateTime.now()));
            logs.setStatus(0);
            logService.save(logs);
        } catch (Exception ex) {
            log.error("日志保存失败{}",ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
