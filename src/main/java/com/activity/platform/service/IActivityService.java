package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Activity;
import com.activity.platform.pojo.ActivityCharacter;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;


public interface IActivityService extends IService<Activity> {
    public Result createActivity(Activity activity);
    public Result createActivity(Activity activity, List<ActivityCharacter> characters);
    public Result activityPage(Integer pageNum, Integer pageSize);
    public String calculateActivityStatus(Activity activity, LocalDateTime now);
    public Result hotActivity();
    public Activity getActivityById(Long activityId) throws NoSuchFieldException, IllegalAccessException;
    public Result updateActivity(Activity activity);
    public Result deleteActivity(Long activityId);
    public void start(Long activityId);
    public void start(List<Long> activityIds);
    public void end(Long activityId);
    

    Result getAllActivities();
    

    Result setHotActivity(Long activityId);
    

    Result removeHotActivity(Long activityId);
    

    Result getAllActivitiesForHotManage();
}
