package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_org")
public class Org {
    // 组织ID
    private Long id;
    // 组织名称
    private String name;
    // 组织描述
    private String description;
}
