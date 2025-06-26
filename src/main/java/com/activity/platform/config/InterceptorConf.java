package com.activity.platform.config;

import com.activity.platform.util.interceptor.LoginInterceptor;
import com.activity.platform.util.interceptor.TokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConf implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor(stringRedisTemplate))
                .addPathPatterns("/**").order(0);
        //TODO 最后进行权限配置
       registry.addInterceptor(new LoginInterceptor(stringRedisTemplate))
                .addPathPatterns(
                        "/user/me",
                        "/user/login",
                        "/vol/get",
                        "/vol/remove"
                        ).order(1);
    }

}
