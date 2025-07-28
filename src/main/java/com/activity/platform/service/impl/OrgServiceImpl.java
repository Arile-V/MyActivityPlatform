package com.activity.platform.service.impl;

import com.activity.platform.dto.Result;
import com.activity.platform.mapper.OrgMapper;
import com.activity.platform.pojo.Org;
import com.activity.platform.service.IOrgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrgServiceImpl extends ServiceImpl<OrgMapper, Org> implements IOrgService {

    @Override
    public Result createOrg(Org org) {
        return null;
    }

    @Override
    public Result deleteOrg(Long id) {
        return null;
    }

    @Override
    public Result updateOrg(Org org) {
        return null;
    }

    @Override
    public Result getOrg(Long id) {
        return null;
    }
}
