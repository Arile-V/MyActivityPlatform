package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Activity;
import com.activity.platform.service.IActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        return activityService.createActivity(activity);
    }

    @PostMapping("/update")
    public Result updateActivity(@RequestBody Activity activity) {
        if (activityService.updateById(activity)){
            return Result.ok();
        }else {
            return Result.fail("更新失败");
        }
    }

    @PostMapping("/delete")
    public Result deleteActivity(@RequestBody Long id) {
        if (activityService.removeById(id)){
            return Result.ok();
        }else
            return Result.fail("删除失败");
    }

    //活动列表页的分页查询接口
    @GetMapping("/list/{pageNum}")
    public Result listActivity(@PathVariable Integer pageNum) {
        return activityService.activityPage(pageNum,10);
    }

    //首页展示的最新活动
    @GetMapping("/list/new")
    public Result listNewActivity() {
        return activityService.activityPage(1,5);
    }

    //首页展示的热点活动(热点活动是指浏览量大的且在缓存中的)
    @GetMapping("/list/hot")
    public Result listHotActivity() {
        return activityService.hotActivity();
    }

    @GetMapping("{id}")
    public Result getActivity(@PathVariable Long id) throws NoSuchFieldException, IllegalAccessException {
        return Result.ok(activityService.getActivityById(id));
    }
}
