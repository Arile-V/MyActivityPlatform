package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.plus.Org2User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IOrg2UserService extends IService<Org2User> {
    public Result userJoin(Long OrgId);
    public Result checkUser(Long OrgId);
    public Result selectUser(Long UserId);
    public Result userQuit(Long OrgId);
    public Result kickOutUser(Long OrgId,Long UserId);
}
