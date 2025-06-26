package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_admin")
public class Admin {
    private Long id;
    private String username;
    private String password;
}
