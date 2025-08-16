package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.service.IActivityCharacterService;
import com.activity.platform.service.IUserService;
import com.activity.platform.service.IVolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "志愿者管理", description = "志愿者相关的接口")
@RestController
@RequestMapping("/volunteer")
public class VolController {
    @Resource
    private IVolService volunteerService;

    @Operation(summary = "获取志愿者信息", description = "根据活动角色ID获取志愿者信息")
    @GetMapping("/get/{activityCharacterId}")
    public Result getVolunteer(
            @Parameter(description = "活动角色ID", required = true, example = "1")
            @PathVariable Long activityCharacterId) {
        return volunteerService.get(activityCharacterId);
    }

    @Operation(summary = "退出志愿者", description = "志愿者退出当前活动角色")
    @PostMapping("/remove/{activityCharacterId}")
    public Result removeVolunteer(
            @Parameter(description = "活动角色ID", required = true, example = "1")
            @PathVariable Long activityCharacterId) {
        return volunteerService.quit(activityCharacterId);
    }

    @Operation(summary = "完成志愿者任务", description = "标记志愿者任务已完成")
    @PostMapping("/finish/{activityCharacterId}")
    public Result finishVolunteer(
            @Parameter(description = "活动角色ID", required = true, example = "1")
            @PathVariable Long activityCharacterId) {
        return volunteerService.check(activityCharacterId);
    }

    @Operation(summary = "获取志愿者列表", description = "获取当前用户的所有志愿者记录")
    @GetMapping("/lists")
    public Result lists() {
        return volunteerService.lists();
    }

    @Operation(summary = "获取志愿者详情", description = "根据志愿者ID获取详细信息")
    @GetMapping("/info/{id}")
    public Result info(
            @Parameter(description = "志愿者ID", required = true, example = "1")
            @PathVariable Long id) {
        return volunteerService.info(id);
    }
}
