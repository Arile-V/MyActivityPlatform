package com.activity.platform.pojo;

import com.activity.platform.config.CustomTimestampDeserializer;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("tb_activity")
public class Activity {
    // 活动ID
    @TableId
    private Long id;
    // 活动名称
    @TableField("name")
    private String name;
    // 活动描述
    @TableField("description")
    private String description;
    // 活动地点
    @TableField("location")
    private String location;
    // 活动状态
    @TableField("status")
    private String status;
    // 活动图片URL
    @TableField("image_url")
    private String imageUrl;
    // 活动类型
    @TableField("type")
    private String type;
    // 活动开始时间
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp startTime;
    // 活动结束时间
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp endTime;
    // 活动领取开始时间
    @TableField("start_to_get_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp startToGetTime;
    // 活动领取结束时间
    @TableField("end_to_get_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp endToGetTime;
    // 活动创建时间
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp createTime;
    
    // 是否为热点活动（非数据库字段，仅用于前端显示）
    @TableField(exist = false)
    private Boolean isHot;
}
