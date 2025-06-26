package com.activity.platform.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_org")
public class Org {
    private Long id;
    private String name;
    private String description;
}
