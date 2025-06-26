package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.mail.MessagingException;

public interface IUserService extends IService<User> {
    public Result sentCode(String username) throws MessagingException;
    public Result login(String username, String code);
}
