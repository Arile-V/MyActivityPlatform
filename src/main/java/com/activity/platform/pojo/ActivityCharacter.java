package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("activity_character")
public class ActivityCharacter {
    // 活动角色ID
    private Long id;
    // 活动ID
    private Long activityId;
    // 角色名称
    private String name;
    // 角色容量
    private Integer volume;
}
