package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_admin")
public class Admin {
    // 管理员ID
    private Long id;
    // 管理员用户名
    private String username;
    // 管理员密码
    private String password;
}
