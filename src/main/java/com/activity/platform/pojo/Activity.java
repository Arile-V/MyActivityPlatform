package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private Timestamp startTime;
    // 活动结束时间
    private Timestamp endTime;
    // 活动领取开始时间
    private Timestamp startToGetTime;
    // 活动领取结束时间
    private Timestamp endToGetTime;
    // 活动创建时间
    private Timestamp createTime;
}
