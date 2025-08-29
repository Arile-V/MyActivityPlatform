package com.activity.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 活动删除请求DTO
 */
@Data
@Schema(description = "活动删除请求")
public class ActivityDeleteRequest {
    
    @NotNull(message = "活动ID不能为空")
    @Schema(description = "活动ID", required = true, example = "1")
    private Long id;
} 