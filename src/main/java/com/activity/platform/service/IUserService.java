package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.mail.MessagingException;
/*
设计思路：
    登录注册采用邮箱验证码
    登录后生成token，token存储在redis中，token过期时间设置为1天
    登录后返回token，前端存储在cookie中，每次请求携带token
    登录后返回用户信息，前端存储在localStorage中
* */
public interface IUserService extends IService<User> {
    public Result sentCode(String username) throws MessagingException;
    public Result login(String username, String code);
    public Result logout(String token);
    public Result sentRegisterCode(User user) throws MessagingException;
    public Result register(String email, String code);
}
