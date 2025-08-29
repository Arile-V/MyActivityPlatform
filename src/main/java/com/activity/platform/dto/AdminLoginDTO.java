package com.activity.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "管理员登录请求参数")
public class AdminLoginDTO {
    
    @Schema(description = "管理员用户名", example = "admin", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @Schema(description = "管理员密码", example = "password123", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;
} 