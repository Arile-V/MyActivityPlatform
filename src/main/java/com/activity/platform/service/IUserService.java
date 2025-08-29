package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.dto.UserLoginDTO;
import com.activity.platform.dto.UserRegisterConfirmDTO;
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
    
    /**
     * 使用完整用户信息进行注册确认
     * @param registerDTO 注册确认请求参数
     * @return 注册结果
     */
    public Result register(UserRegisterConfirmDTO registerDTO);
    
    /**
     * 使用DTO进行用户登录
     * @param loginDTO 登录请求参数
     * @return 登录结果
     */
    public Result login(UserLoginDTO loginDTO);
    
    /**
     * 查询所有用户列表
     * @return 用户列表
     */
    public Result getAllUsers();
    
    /**
     * 根据条件查询用户
     * @param email 邮箱
     * @param username 用户名
     * @param schoolId 学号
     * @return 查询结果
     */
    public Result searchUsers(String email, String username, String schoolId);
    
    /**
     * 根据token获取当前用户信息
     * @param token 登录令牌
     * @return 用户信息
     */
    public Result getUserInfo(String token);
}
