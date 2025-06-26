package com.activity.platform.pojo.plus;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("org2admin")
public class Org2Admin {
    private Long id;
    private Long orgId;
    private Long adminId;
}
