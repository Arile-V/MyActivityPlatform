package com.activity.platform.util.interceptor;

import cn.hutool.json.JSONUtil;
import com.activity.platform.dto.UserDTO;
import com.activity.platform.pojo.Admin;
import com.activity.platform.util.AdminHolder;
import com.activity.platform.util.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class TokenInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public TokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {
        String token = request.getHeader("token");
        if (!StringUtils.isEmpty(token)) {
            String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
            if (userJson != null) {
                UserDTO user = JSONUtil.toBean(userJson, UserDTO.class);
                UserHolder.setUser(user);
            }
            String adminJson = stringRedisTemplate.opsForValue().get("admin:login:" + token);
            if (adminJson != null) {
                AdminHolder.save(JSONUtil.toBean(adminJson, Admin.class, false));
            }
        }
        HandlerInterceptor.super.preHandle(request, response, handler);
        return true;
    }

    @Override
    public void postHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            ModelAndView modelAndView) throws Exception {
        if (UserHolder.exists()) {
            UserHolder.remove();
        }
        if (AdminHolder.exist()) {
            AdminHolder.remove();
        }
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
