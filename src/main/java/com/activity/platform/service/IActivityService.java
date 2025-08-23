package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Activity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/*
设计思路：
    活动发布，活动列表，活动详情，活动修改，活动删除
    活动列表分页，活动列表按照时间排序，活动列表按照热度排序
    活动详情包括活动基本信息，活动报名人数，活动报名状态，活动报名，活动取消报名，活动报名人数限制
* */
public interface IActivityService extends IService<Activity> {
    public Result createActivity(Activity activity);
    public Result activityPage(Integer pageNum, Integer pageSize);
    public Result hotActivity();
    public Activity getActivityById(Long activityId) throws NoSuchFieldException, IllegalAccessException;
    public Result updateActivity(Activity activity);
    public Result deleteActivity(Long activityId);
    public void start(Long activityId);
    public void start(List<Long> activityIds);
    public void end(Long activityId);
    
    /**
     * 查询所有活动（用于调试）
     * @return 所有活动列表
     */
    Result getAllActivities();
}
