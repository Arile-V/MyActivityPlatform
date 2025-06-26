package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("tb_user")
public class User {
    @TableId
    private Long id;

    private String username;
    private String email;
    private Timestamp createTime;
    private Long workingHours;
    private String schoolID;
    private String name;
}
