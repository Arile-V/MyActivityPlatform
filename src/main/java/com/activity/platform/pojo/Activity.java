package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
@Data
@TableName("tb_activity")
public class Activity {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String status;
    private String imageUrl;
    private String type;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp startToGetTime;
    private Timestamp endToGetTime;
    private Timestamp createTime;
}
