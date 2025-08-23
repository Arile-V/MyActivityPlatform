package com.activity.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "用户注册确认请求参数")
public class UserRegisterConfirmDTO {
    
    @Schema(description = "邮箱地址", example = "user@example.com", required = true)
    @NotBlank(message = "邮箱地址不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Schema(description = "用户名", example = "username123", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;
    
    @Schema(description = "真实姓名", example = "张三", required = true)
    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String name;
    
    @Schema(description = "学号", example = "STU2024001", required = true)
    @NotBlank(message = "学号不能为空")
    @Size(max = 50, message = "学号长度不能超过50个字符")
    private String schoolID;
    
    @Schema(description = "验证码", example = "123456", required = true)
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须是6位数字")
    private String code;
} 