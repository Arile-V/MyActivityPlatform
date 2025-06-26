package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
@Data
@TableName("notification")
public class Notification {
    @TableId
    private Long id;

    private String title;
    private String content;
    private Timestamp createTime;
}
