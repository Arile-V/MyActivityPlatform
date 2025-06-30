package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.plus.Org2User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IOrg2UserService extends IService<Org2User> {
    public Result userJoin(Long OrgId);
}
