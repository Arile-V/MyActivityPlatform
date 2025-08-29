package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.activity.platform.config.StringToLongDeserializer;
import lombok.Data;

@Data
@TableName("activity_character")
public class ActivityCharacter {
    // 活动角色ID
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = StringToLongDeserializer.class)
    private Long id;
    // 活动ID
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = StringToLongDeserializer.class)
    private Long activityId;
    // 角色名称 - 映射到数据库的character_name字段
    @TableField("character_name")
    private String name;
    // 角色容量
    private Integer volume;
}
