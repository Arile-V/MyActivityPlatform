package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.log.MyLog;
import com.activity.platform.pojo.ActivityCharacter;
import com.activity.platform.service.IActivityCharacterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "活动角色管理", description = "活动角色相关的接口")
@RestController
@RequestMapping("/activityCharacter")
public class ActivityCharacterController {
    @Resource
    private IActivityCharacterService activityCharacterService;

    @Operation(summary = "创建活动角色", description = "创建一个新的活动角色")
    @PostMapping("/create/character")
    public Result create(
            @Parameter(description = "活动角色信息", required = true)
            @RequestBody ActivityCharacter activityCharacter) {
        return activityCharacterService.create(activityCharacter);
    }

    @Operation(summary = "更新活动角色", description = "更新已存在的活动角色信息")
    @PostMapping("/update/character")
    public Result update(
            @Parameter(description = "活动角色信息", required = true)
            @RequestBody ActivityCharacter activityCharacter) {
        return activityCharacterService.update(activityCharacter);
    }

    @Operation(summary = "删除活动角色", description = "根据ID删除活动角色")
    @PostMapping("/delete/character")
    public Result delete(
            @Parameter(description = "活动角色ID", required = true)
            @RequestBody Long characterId) {
        return activityCharacterService.delete(characterId);
    }
}
