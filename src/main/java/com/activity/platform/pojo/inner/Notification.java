package com.activity.platform.pojo.inner;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
@Data
@TableName("tb_notification")
public class Notification {
    @TableId
    private Long id;

    private String title;
    private String content;
    private Timestamp createTime;
}
