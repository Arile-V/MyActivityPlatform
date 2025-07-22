package com.activity.platform.service.impl;

import com.activity.platform.mapper.OrgMapper;
import com.activity.platform.pojo.Org;
import com.activity.platform.service.IOrgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrgServiceImpl extends ServiceImpl<OrgMapper, Org> implements IOrgService {

}
