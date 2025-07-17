package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Activity;
import com.activity.platform.service.IActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/activity")
public class ActivityController {
    private final IActivityService activityService;

    public ActivityController(IActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/create")
    public Result createActivity(@RequestBody Activity activity) {
        return Result.ok();
    }

    //TODO 活动列表页的分页查询接口
    @GetMapping("/list/{}")
    public Result listActivity() {
        return Result.ok();
    }

    //TODO 首页展示的最新活动
    @GetMapping("/list/new")
    public Result listNewActivity() {
        return Result.ok();
    }

    //TODO 首页展示的特殊活动
    @GetMapping("/list/hot")
    public Result listHotActivity() {
        return Result.ok();
    }
}
