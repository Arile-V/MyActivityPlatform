package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.service.IActivityCharacterService;
import com.activity.platform.service.IUserService;
import com.activity.platform.service.IVolService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/volunteer")
public class VolController {
    @Resource
    private IVolService volunteerService;

    @PostMapping("/get")
    public Result getVolunteer(Long activityCharacterId) {
        return volunteerService.get(activityCharacterId);
    }

    @PostMapping("/remove")
    public Result removeVolunteer(Long activityCharacterId) {
        return volunteerService.quit(activityCharacterId);
    }

    @PostMapping("/finish")
    public Result finishVolunteer(Long activityCharacterId) {return volunteerService.check(activityCharacterId);}

    @GetMapping("/lists")
    public Result lists() {
        return volunteerService.lists();
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable Long id) {
        return volunteerService.info(id);
    }
}
