package com.activity.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "用户登录请求参数")
public class UserLoginDTO {
    
    @Schema(description = "用户名或邮箱", example = "username123", required = true)
    @NotBlank(message = "用户名或邮箱不能为空")
    private String loginUser;
    
    @Schema(description = "验证码", example = "123456", required = true)
    @NotBlank(message = "验证码不能为空")
    private String code;
} 