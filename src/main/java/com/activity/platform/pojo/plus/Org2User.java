package com.activity.platform.pojo.plus;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("org2user")
public class Org2User {
    private Long id;
    private Long orgId;
    private Long userId;
}
