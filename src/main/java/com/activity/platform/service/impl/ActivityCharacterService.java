package com.activity.platform.service.impl;

import com.activity.platform.dto.Result;
import com.activity.platform.mapper.ActivityCharacterMapper;
import com.activity.platform.pojo.ActivityCharacter;
import com.activity.platform.service.IActivityCharacterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ActivityCharacterService extends ServiceImpl<ActivityCharacterMapper, ActivityCharacter> implements IActivityCharacterService{

    private final StringRedisTemplate stringRedisTemplate;

    public ActivityCharacterService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Result create(ActivityCharacter activityCharacter) {
        return null;
    }

    @Override
    public Result delete(Long id) {
        return null;
    }

    @Override
    public Result update(ActivityCharacter activityCharacter) {
        return null;
    }

    @Override
    public Result query(Long id) {
        return null;
    }
    //
}
