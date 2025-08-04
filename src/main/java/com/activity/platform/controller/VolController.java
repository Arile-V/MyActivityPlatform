package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.service.IActivityCharacterService;
import com.activity.platform.service.IUserService;
import com.activity.platform.service.IVolService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/volunteer")
public class VolController {
    @Resource
    private IVolService volunteerService;
    @Resource
    private IUserService userService;
    @Resource
    private IActivityCharacterService activityCharacterService;

    @PostMapping("/get")
    public Result getVolunteer(Long activityCharacterId) {
        return volunteerService.get(activityCharacterId);
    }

    @PostMapping("/remove")
    public Result removeVolunteer() {
        return Result.ok();
    }

    @PostMapping("/finish")
    public Result finishVolunteer() {return Result.ok();}
}
