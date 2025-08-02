package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Org;
import com.baomidou.mybatisplus.extension.service.IService;
/*
设计思路：
    创建组织的时候需要管理员审核
    组织管理员可以创建活动
    组织管理员可以管理组织信息
    组织管理员可以管理组织成员
    组织成员可以退出组织
* */
public interface IOrgService extends IService<Org> {
    public Result createOrg(Org org);
    public Result deleteOrg(Long id);
    public Result updateOrg(Org org);
    public Result getOrg(Long id);
}
