package com.activity.platform.pojo;

import com.activity.platform.config.CustomTimestampDeserializer;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.activity.platform.config.StringToLongDeserializer;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("tb_activity")
public class Activity {

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = StringToLongDeserializer.class)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("location")
    private String location;

    @TableField("status")
    private String status;

    @TableField("image_url")
    private String imageUrl;

    @TableField("type")
    private String type;

    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp startTime;

    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp endTime;

    @TableField("start_to_get_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp startToGetTime;

    @TableField("end_to_get_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp endToGetTime;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp createTime;
    

    @TableField(exist = false)
    private Boolean isHot;
}
