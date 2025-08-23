package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Activity;
import com.activity.platform.service.IActivityCharacterService;
import com.activity.platform.service.IActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "活动管理", description = "活动相关的接口")
@RestController
@RequestMapping("/activity")
public class ActivityController {
    private final IActivityService activityService;
    private final IActivityCharacterService activityCharacterService;

    public ActivityController(IActivityService activityService, IActivityCharacterService activityCharacterService) {
        this.activityService = activityService;
        this.activityCharacterService = activityCharacterService;
    }

    @Operation(summary = "创建活动", description = "创建一个新的活动")
    @PostMapping("/create")
    public Result createActivity(
            @Parameter(description = "活动信息", required = true)
            @RequestBody Activity activity) {
        return activityService.createActivity(activity);
    }

    @Operation(summary = "更新活动", description = "更新已存在的活动信息")
    @PostMapping("/update")
    public Result updateActivity(
            @Parameter(description = "活动信息", required = true)
            @RequestBody Activity activity) {
        if (activityService.updateById(activity)){
            return Result.ok();
        }else {
            return Result.fail("更新失败");
        }
    }

    @Operation(summary = "删除活动", description = "根据ID删除活动")
    @PostMapping("/delete")
    public Result deleteActivity(
            @Parameter(description = "活动ID", required = true, example = "1")
            @RequestBody Long id) {
        if (activityService.removeById(id)){
            return Result.ok();
        }else
            return Result.fail("删除失败");
    }

    @Operation(summary = "获取活动列表", description = "分页查询活动列表")
    @GetMapping("/list/{pageNum}")
    public Result listActivity(
            @Parameter(description = "页码", required = true, example = "1")
            @PathVariable Integer pageNum,
            @Parameter(description = "每页大小", required = false, example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return activityService.activityPage(pageNum, pageSize);
    }

    @Operation(summary = "获取最新活动", description = "获取最新发布的活动列表")
    @GetMapping("/list/new")
    public Result listNewActivity() {
        return activityService.activityPage(1,5);
    }

    @Operation(summary = "获取热点活动", description = "获取浏览量最高的活动列表")
    @GetMapping("/list/hot")
    public Result listHotActivity() {
        return activityService.hotActivity();
    }

    @Operation(summary = "获取活动详情", description = "根据ID获取活动详细信息")
    @GetMapping("{id}")
    public Result getActivity(
            @Parameter(description = "活动ID", required = true)
            @PathVariable Long id) throws NoSuchFieldException, IllegalAccessException {
        return Result.ok(activityService.getActivityById(id));
    }

    @Operation(summary = "获取活动角色", description = "获取指定活动的所有角色信息")
    @GetMapping("/character/{id}")
    public Result getActivityCharacter(
            @Parameter(description = "活动ID", required = true)
            @PathVariable Long id) {
        return activityCharacterService.queryByActivity(id);
    }

    @Operation(summary = "查询所有活动（调试用）", description = "查询数据库中所有活动，用于调试")
    @GetMapping("/debug/all")
    public Result getAllActivities() {
        return activityService.getAllActivities();
    }

    @Operation(summary = "查询活动总数（调试用）", description = "查询数据库中活动总数，用于调试")
    @GetMapping("/debug/count")
    public Result getActivityCount() {
        try {
            long count = activityService.count();
            return Result.ok("活动总数: " + count);
        } catch (Exception e) {
            return Result.fail("查询活动总数失败: " + e.getMessage());
        }
    }
}
