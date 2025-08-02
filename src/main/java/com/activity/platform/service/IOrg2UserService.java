package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.plus.Org2User;
import com.baomidou.mybatisplus.extension.service.IService;

/*
设计思路：
    机构当中填充用户，用户加入机构，机构踢出用户，用户退出机构
* */
public interface IOrg2UserService extends IService<Org2User> {
    public Result userJoin(Long OrgId);
    public Result checkUser(Long OrgId);
    public Result selectUser(Long UserId);
    public Result userQuit(Long OrgId);
    public Result kickOutUser(Long OrgId,Long UserId);
}
