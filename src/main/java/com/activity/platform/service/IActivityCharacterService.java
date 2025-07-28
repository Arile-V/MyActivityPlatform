package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.ActivityCharacter;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IActivityCharacterService extends IService <ActivityCharacter>{
    public Result create(ActivityCharacter activityCharacter);
    public Result delete(Long id);
    public Result update(ActivityCharacter activityCharacter);
    public Result queryByActivity(Long id);
}
