package com.activity.platform.config;


import com.activity.platform.dto.Result;
import com.activity.platform.service.ILogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.activity.platform.pojo.inner.MyLog;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {
    @Resource
    private ILogService logService;
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error(e.toString(), e);
        MyLog errorLog = new MyLog();
        errorLog.setException(e.getMessage());
        errorLog.setTime(Timestamp.valueOf(LocalDateTime.now()));
        errorLog.setStatus(1);
        logService.save(errorLog);
        return Result.fail("服务器异常");
    }
}
