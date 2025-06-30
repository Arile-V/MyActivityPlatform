package com.activity.platform.util.interceptor;

import com.activity.platform.pojo.Admin;
import com.activity.platform.util.AdminHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        Admin admin = AdminHolder.get();
        if(admin == null){
            return false;
        }
        HandlerInterceptor.super.preHandle(request, response, handler);
        return true;
    }
}
