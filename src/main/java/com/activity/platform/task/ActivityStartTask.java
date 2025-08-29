package com.activity.platform.task;

import com.activity.platform.enums.ActivityStatus;
import com.activity.platform.pojo.Activity;
import com.activity.platform.service.IActivityService;
import com.activity.platform.service.IVolService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivityStartTask {
    @Resource
    private final IActivityService activityService;
    @Resource
    private final IVolService volService;

    public ActivityStartTask(IActivityService activityService, IVolService volService) {
        this.activityService = activityService;
        this.volService = volService;
    }

    @Scheduled(cron = "0 * * * * ?")
    public void start() {
        LambdaQueryWrapper<Activity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Activity::getStatus, "0").le(Activity::getStartTime, Timestamp.valueOf(LocalDateTime.now()));
        List<Activity> activities = activityService.list(lambdaQueryWrapper);
        if (!activities.isEmpty() && activities.size() < 10) {
            activities.stream().forEach(activity -> {
                activityService.start(activity.getId());
                volService.start(activity.getId());
            });
        } else if (activities.size()>=10) {
            activityService.start(activities.stream().map(Activity::getId).collect(Collectors.toList()));
            volService.start(activities.stream().map(Activity::getId).collect(Collectors.toList()));
        }

    }

    @Scheduled(cron = "0 * * * * ?")
    public void badVol() {

        LambdaQueryWrapper<Activity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(Activity::getId).eq(Activity::getStatus, ActivityStatus.END.name());
        List<Activity> list = activityService.list(lambdaQueryWrapper);
        volService.badVol(list.stream().map(Activity::getId).collect(Collectors.toList()));

        LambdaUpdateWrapper<Activity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(Activity::getStatus, ActivityStatus.END.name())
                .eq(Activity::getStatus, "1")
                .lt(Activity::getEndTime, new Timestamp(System.currentTimeMillis()));

        activityService.update(lambdaUpdateWrapper);
    }
}
