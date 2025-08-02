package com.activity.platform.task;

import com.activity.platform.pojo.Activity;
import com.activity.platform.service.IActivityService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivityStartTask {
    @Resource
    private IActivityService activityService;
    @Scheduled(cron = "0 * * * * ?")
    public void start() {
        LambdaQueryWrapper<Activity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Activity::getStatus, 0).le(Activity::getStartTime, System.currentTimeMillis());
        List<Activity> activities = activityService.list(lambdaQueryWrapper);
        if (!activities.isEmpty() && activities.size() < 10) {
            activities.stream().forEach(activity -> {
                activityService.start(activity.getId());
            });
        } else if (activities.size()>=10) {
            activityService.start(activities.stream().map(Activity::getId).collect(Collectors.toList()));
        }
        //还有邮件发送或者站内推送
    }
}
