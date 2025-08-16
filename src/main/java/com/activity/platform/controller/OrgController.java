package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Org;
import com.activity.platform.service.IOrgService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/Org")
public class OrgController {
    @Resource
    private IOrgService orgService;

    @PostMapping("/Create")
    public Result Create(@RequestBody Org org){
        return orgService.createOrg(org);
    }
}
