package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("activity_character")
public class ActivityCharacter {
    private Long id;
    private Long activityId;
    private String name;
    private Integer volume;
}
