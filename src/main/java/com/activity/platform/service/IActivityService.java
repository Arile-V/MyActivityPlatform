package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Activity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IActivityService extends IService<Activity> {
    public Result createActivity(Activity activity);
}
