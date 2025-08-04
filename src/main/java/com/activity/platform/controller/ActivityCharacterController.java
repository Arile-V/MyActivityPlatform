package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/activityCharacter")
//活动角色
public class ActivityCharacterController {

    @PostMapping("/create")
    public Result create(){
        return Result.ok();
    }

    @PostMapping("/update")
    public Result update(){
        return Result.ok();
    }

    @PostMapping("/delete")
    public Result delete(){
        return Result.ok();
    }

}
