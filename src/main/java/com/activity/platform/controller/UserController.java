package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.dto.UserRegisterConfirmDTO;
import com.activity.platform.dto.UserLoginDTO;
import com.activity.platform.pojo.User;

import com.activity.platform.service.IUserService;
import com.activity.platform.util.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理", description = "用户相关的接口")
@RestController
@RequestMapping("/user")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "发送注册验证码", description = "发送注册验证码到用户邮箱")
    @PostMapping("/register/code")
    public Result registerCode(
            @Parameter(description = "用户信息", required = true)
            @RequestBody User user) throws MessagingException {
        return userService.sentRegisterCode(user);
    }

    @Operation(summary = "确认注册", description = "使用完整用户信息和验证码完成注册")
    @PostMapping("/register/confirm")
    public Result registerConfirm(
            @Parameter(description = "用户注册确认信息", required = true)
            @Valid @RequestBody UserRegisterConfirmDTO registerDTO) {
        return userService.register(registerDTO);
    }





    @Operation(summary = "用户退出登录", description = "用户退出当前登录状态")
    @PostMapping("/logout")
    public Result logout(
            @Parameter(description = "登录令牌", required = true)
            @RequestHeader String token) {
        return userService.logout(token);
    }

    @Operation(summary = "发送登录验证码", description = "发送登录验证码到用户邮箱")
    @PostMapping("/login/code")
    public Result userCode(
            @Parameter(description = "用户名/邮箱", required = true, example = "user@example.com")
            @RequestBody String username) throws MessagingException {
        return userService.sentCode(username);
    }

    @Operation(summary = "用户登录", description = "使用用户名/邮箱和验证码登录")
    @PostMapping("/login")
    public Result userLogin(
            @Parameter(description = "用户登录信息", required = true)
            @Valid @RequestBody UserLoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @Operation(summary = "获取当前用户信息", description = "根据token获取当前登录用户的详细信息")
    @GetMapping("/info")
    public Result getUserInfo(
            @Parameter(description = "登录令牌", required = true)
            @RequestHeader String token) {
        return userService.getUserInfo(token);
    }

    @Operation(summary = "查询所有用户", description = "获取系统中所有用户列表")
    @GetMapping("/users")
    public Result getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "条件查询用户", description = "根据邮箱、用户名或学号查询用户")
    @GetMapping("/users/search")
    public Result searchUsers(
            @Parameter(description = "邮箱地址", required = false, example = "user@example.com")
            @RequestParam(required = false) String email,
            @Parameter(description = "用户名", required = false, example = "username123")
            @RequestParam(required = false) String username,
            @Parameter(description = "学号", required = false, example = "STU2024001")
            @RequestParam(required = false) String schoolId) {
        return userService.searchUsers(email, username, schoolId);
    }
}
