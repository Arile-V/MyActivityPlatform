package com.activity.platform.service.impl;

import cn.hutool.json.JSONUtil;
import com.activity.platform.dto.Result;
import com.activity.platform.mapper.AdminMapper;
import com.activity.platform.pojo.Admin;
import com.activity.platform.service.IAdminService;
import com.activity.platform.service.IOrg2AdminService;
import com.activity.platform.util.AdminHolder;
import com.activity.platform.util.CacheUtil;
import com.activity.platform.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SnowflakeIdWorker idWorker;
    @Resource
    private IOrg2AdminService org2AdminService;
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

    @Override
    public Result register(Admin admin,Long OrgId) {
        if (org2AdminService.query().eq("org_id",OrgId).one() != null){
            return Result.fail("该组织已存在管理员");
        }
        admin.setId(idWorker.nextId());
        save(admin);
        return Result.ok();
    }

    @Override
    public Result logout() {
        stringRedisTemplate.delete("login:admin:"+ AdminHolder.get().getId());
        return Result.ok();
    }
}
