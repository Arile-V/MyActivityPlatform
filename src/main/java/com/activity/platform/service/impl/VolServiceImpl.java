package com.activity.platform.service.impl;

import com.activity.platform.dto.Result;
import com.activity.platform.mapper.VolMapper;
import com.activity.platform.pojo.plus.Vol;
import com.activity.platform.service.IVolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class VolServiceImpl extends ServiceImpl<VolMapper, Vol> implements IVolService {

    @Override
    public Result addVol(Long ActivityID) { //下单用

        return null;
    }
}
