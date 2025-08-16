package com.activity.platform.controller;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Org;
import com.activity.platform.service.IOrgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "组织管理", description = "组织相关的接口")
@RestController
@RequestMapping("/Org")
public class OrgController {
    @Resource
    private IOrgService orgService;

    @Operation(summary = "创建组织", description = "创建一个新的组织")
    @PostMapping("/Create")
    public Result Create(
            @Parameter(description = "组织信息", required = true)
            @RequestBody Org org) {
        return orgService.createOrg(org);
    }
}
