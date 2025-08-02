package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.plus.Org2Admin;
import com.baomidou.mybatisplus.extension.service.IService;

/*
设计思路：
    机构管理员
    由超管创建，超管是个固定角色
* */
public interface IOrg2AdminService extends IService<Org2Admin> {
    Result sendMessage(Long OrgId);
}
