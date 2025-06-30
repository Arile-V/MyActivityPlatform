package com.activity.platform.service.impl;

import cn.hutool.json.JSONUtil;
import com.activity.platform.dto.Result;
import com.activity.platform.mapper.AdminMapper;
import com.activity.platform.pojo.Admin;
import com.activity.platform.service.IAdminService;
import com.activity.platform.util.CacheUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;

public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result login(String username, String password) {
        Admin admin = query().eq("username", username).eq("password", password).one();
        if (admin == null) {
            return Result.fail("用户名或密码错误");
        }else{
            String token = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("login:admin:"+token, JSONUtil.toJsonStr(admin));
            return Result.ok(token);
        }
    }
}
