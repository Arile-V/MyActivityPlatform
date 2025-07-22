package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Activity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IActivityService extends IService<Activity> {
    public Result createActivity(Activity activity);
    public Result activityPage(Integer pageNum, Integer pageSize);
    public Result hotActivity();
    public Activity getActivityById(Long activityId) throws NoSuchFieldException, IllegalAccessException;
    public Result updateActivity(Activity activity);
    public Result deleteActivity(Long activityId);
}
