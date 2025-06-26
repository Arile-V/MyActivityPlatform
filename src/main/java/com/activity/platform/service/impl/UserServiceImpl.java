package com.activity.platform.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.activity.platform.dto.Result;
import com.activity.platform.dto.UserDTO;
import com.activity.platform.mapper.UserMapper;
import com.activity.platform.pojo.User;
import com.activity.platform.service.IUserService;
import com.activity.platform.service.IJavaMailService;
import com.activity.platform.util.EmailCode;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private final StringRedisTemplate stringRedisTemplate;

    public UserServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Resource
    private IJavaMailService javaMailService;

    @Override
    public Result sentCode(String username) throws MessagingException {
        String code = EmailCode.randomCode();
        if(query().eq("username",username).one()==null){
            return Result.fail("用户不存在");
        }
        if(stringRedisTemplate.opsForValue().setIfAbsent("loginForm:"+username,code, 60*5, TimeUnit.SECONDS)){
            javaMailService.sendEmailCode(code);
            return Result.ok("发送成功");
        }else{
            return Result.fail("请勿频繁发送验证码");
        }
    }

    @Override
    public Result login(String username, String code) {
        String cacheCode = stringRedisTemplate.opsForValue().get("loginForm:"+username);
        if (cacheCode == null){
            return Result.fail("请先获取验证码");
        }else if(!cacheCode.equals(code)){
            return Result.fail("验证码错误");
        }else{
            User user = query().eq("username",username).one();
            if (user == null){
                return Result.fail("用户不存在");
            }else{
                UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
                stringRedisTemplate.delete("loginForm:"+username);
                String token = UUID.randomUUID().toString();
                stringRedisTemplate.opsForValue().set("user:token:"+token, JSONUtil.toJsonStr(userDTO), 60*60*24, TimeUnit.SECONDS);
                return Result.ok(token);
            }
        }
    }
}
