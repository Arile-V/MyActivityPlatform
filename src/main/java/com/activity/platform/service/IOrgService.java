package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Org;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IOrgService extends IService<Org> {
    public Result createOrg(Org org);
    public Result deleteOrg(Long id);
    public Result updateOrg(Org org);
    public Result getOrg(Long id);
}
