package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.service.IOrgService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/Org")
public class OrgController {
    @Resource
    private IOrgService orgService;

    @PostMapping("/Create")
    public Result Create(){
        return Result.ok();
    }
}
