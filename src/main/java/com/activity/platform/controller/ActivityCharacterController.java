package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.ActivityCharacter;
import com.activity.platform.service.IActivityCharacterService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/activityCharacter")
//活动角色
public class ActivityCharacterController {
    @Resource
    private IActivityCharacterService activityCharacterService;

    @PostMapping("/create/character")
    public Result create(@RequestBody ActivityCharacter activityCharacter){
        return activityCharacterService.create(activityCharacter);
    }

    @PostMapping("/update/character")
    public Result update(@RequestBody ActivityCharacter activityCharacter){
        return activityCharacterService.update(activityCharacter);
    }

    @PostMapping("/delete/character")
    public Result delete(@RequestBody Long characterId){
        return activityCharacterService.delete(characterId);
    }

}
