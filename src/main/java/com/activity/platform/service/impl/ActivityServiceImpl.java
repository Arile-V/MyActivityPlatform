package com.activity.platform.service.impl;

import com.activity.platform.dto.Result;
import com.activity.platform.mapper.ActivityMapper;
import com.activity.platform.pojo.Activity;
import com.activity.platform.service.IActivityService;
import com.activity.platform.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {
    private final SnowflakeIdWorker idWorker;

    public ActivityServiceImpl(SnowflakeIdWorker idWorker) {this.idWorker = idWorker;}

    @Override
    @Transactional
    public Result createActivity(Activity activity) {
        Long activityId = idWorker.nextId();
        activity.setId(activityId);
        activity.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        boolean isSuccess = save(activity);
        return Result.ok(activityId);
    }
}
