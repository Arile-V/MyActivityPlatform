package com.activity.platform.controller;

import com.activity.platform.dto.Result;

import com.activity.platform.service.IAdminService;
import com.activity.platform.service.IUserService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/login")
public class LoginController {
    @Resource
    IUserService userService;
    @Resource
    IAdminService adminService;

    @PostMapping("/user/code")
    public Result userCode(@RequestBody String username) throws MessagingException {
        return userService.sentCode(username);
    }
    @PostMapping("/user")
    public Result userLogin(@RequestBody String username, @RequestBody String code){
        return userService.login(username,code);
    }

    @PostMapping("/admin")
    public Result adminLogin(@RequestBody String userName , @RequestBody String password){
        return adminService.login(userName,password);
    }
}
