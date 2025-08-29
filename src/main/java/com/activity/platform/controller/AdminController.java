package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.dto.AdminLoginDTO;
import com.activity.platform.pojo.Admin;
import com.activity.platform.service.IAdminService;
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

    @Operation(summary = "管理员登录", description = "管理员用户名密码登录")
    @PostMapping("/login")
    public Result adminLogin(
            @Parameter(description = "管理员登录信息", required = true)
            @RequestBody AdminLoginDTO adminLoginDTO) {
        return adminService.login(adminLoginDTO.getUsername(), adminLoginDTO.getPassword());
    }

    @Operation(summary = "管理员退出登录", description = "管理员退出当前登录状态")
    @PostMapping("/logout")
    public Result adminLogout() {
        return adminService.logout();
    }


}
