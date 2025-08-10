package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Admin;
import com.activity.platform.service.IAdminService;
import com.activity.platform.service.IOrg2AdminService;
import com.activity.platform.service.IOrg2UserService;
import com.activity.platform.util.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/admin")
public class AdminController {
    @Resource
    private IAdminService adminService;
    @Resource
    private IOrg2AdminService org2AdminService;
    @Resource
    private IOrg2UserService org2UserService;

    @PostMapping("/login/admin")
    public Result adminLogin(@RequestBody String userName , @RequestBody String password){
        return adminService.login(userName,password);
    }
    @PostMapping("/logout/admin")
    public Result adminLogout(){
        return adminService.logout();
    }
    @PostMapping("/register/admin")
    public Result adminRegister(Admin admin,Long OrgId){
        return adminService.register(admin,OrgId);
    }
    @PostMapping("/user2org/admin")
    public Result registerUser2Org(Long OrgId){
        return org2UserService.checkUser(OrgId);
    }
}
