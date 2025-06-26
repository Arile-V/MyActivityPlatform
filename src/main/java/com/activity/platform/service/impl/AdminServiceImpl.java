package com.activity.platform.service.impl;

import com.activity.platform.dto.Result;
import com.activity.platform.mapper.AdminMapper;
import com.activity.platform.pojo.Admin;
import com.activity.platform.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Override
    public Result login(String username, String password) {
        return null;
    }
}
