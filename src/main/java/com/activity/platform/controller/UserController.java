package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.User;
import com.activity.platform.service.IOrg2UserService;
import com.activity.platform.service.IUserService;
import com.activity.platform.util.UserHolder;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/register")
public class UserController {
    private final IUserService userService;
    private final IOrg2UserService org2UserService;

    public UserController(IUserService userService, IOrg2UserService org2UserService) {
        this.userService = userService;
        this.org2UserService = org2UserService;
    }

    @PostMapping("/user/code")
    public Result registerCode(@RequestBody User user) throws MessagingException {
        return userService.sentRegisterCode(user);
    }

    @PostMapping("/user/confirm")
    public Result registerConfirm(@RequestBody String email, @RequestBody String code) {
        return userService.register(email, code);
    }

    @PostMapping("/user2org")
    public Result registerUser2Org() {
        return org2UserService.userJoin(UserHolder.getUser().getId());
    }

    @GetMapping("/user/org/{id}")
    public Result checkUser(@PathVariable Long id) {
        return org2UserService.checkUser(id);
    }

    @PostMapping("/logout")
    public Result logout(@RequestHeader String token) {
        return userService.logout(token);
    }
}
