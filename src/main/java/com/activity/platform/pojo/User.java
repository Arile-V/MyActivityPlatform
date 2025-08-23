package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("tb_user") // 表名
public class User {
    @TableId // 主键
    private Long id;
    private String username; // 用户名
    private String email; // 邮箱
    private Timestamp createTime; // 创建时间
    private Long workingHours; // 工作时长
    private String schoolId; // 学校ID
    private String name; // 姓名
}
