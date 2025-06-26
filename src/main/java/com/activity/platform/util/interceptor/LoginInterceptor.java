package com.activity.platform.util.interceptor;

import cn.hutool.json.JSONUtil;
import com.activity.platform.dto.UserDTO;
import com.activity.platform.util.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {
        HandlerInterceptor.super.preHandle(request, response, handler);
        if(!UserHolder.exists()){
            return false;
        }else {
            return true;
        }
    }
}
