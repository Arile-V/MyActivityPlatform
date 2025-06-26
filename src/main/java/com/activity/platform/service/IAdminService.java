package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IAdminService extends IService<Admin> {
    Result login(String username, String password);
}
