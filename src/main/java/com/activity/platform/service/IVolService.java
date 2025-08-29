package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Activity;
import com.activity.platform.pojo.plus.Vol;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IVolService extends IService<Vol>{
    Result get(Long characterId, Long activityId, String email);

    Result quit(Long characterId, String email);

    Result check(Long characterId, String email);

    // 用户看自己参与过的活动总览
    Result lists(String email);

    // 用户看自己参与过的活动详情
    Result info(Long characterId);

    // 检查用户是否已报名指定活动角色
    Result checkUserSignUp(Long activityCharacterId, String email);

    void start(Long activityId);
    void start(List<Long> activityIds);

    void badVol(List<Long> activityIds);
}
