package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Admin;
import com.activity.platform.service.IAdminService;
import com.activity.platform.service.IOrg2AdminService;
import com.activity.platform.service.IOrg2UserService;
import com.activity.platform.util.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理员管理", description = "管理员相关的接口")
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private IAdminService adminService;
    @Resource
    private IOrg2AdminService org2AdminService;
    @Resource
    private IOrg2UserService org2UserService;

    @Operation(summary = "管理员登录", description = "管理员用户名密码登录")
    @PostMapping("/login/admin")
    public Result adminLogin(
            @Parameter(description = "用户名", required = true)
            @RequestBody String userName,
            @Parameter(description = "密码", required = true)
            @RequestBody String password) {
        return adminService.login(userName, password);
    }

    @Operation(summary = "管理员退出登录", description = "管理员退出当前登录状态")
    @PostMapping("/logout/admin")
    public Result adminLogout() {
        return adminService.logout();
    }

    @Operation(summary = "管理员注册", description = "注册新的管理员账号")
    @PostMapping("/register/admin")
    public Result adminRegister(
            @Parameter(description = "管理员信息", required = true)
            @RequestBody Admin admin,
            @Parameter(description = "组织ID", required = true, example = "1")
            @RequestBody Long OrgId) {
        return adminService.register(admin, OrgId);
    }

    @Operation(summary = "用户加入组织", description = "当前用户加入指定组织")
    @PostMapping("/user2org/admin")
    public Result registerUser2Org(
            @Parameter(description = "组织ID", required = true, example = "1")
            @RequestBody Long OrgId) {
        return org2UserService.checkUser(OrgId);
    }
}
