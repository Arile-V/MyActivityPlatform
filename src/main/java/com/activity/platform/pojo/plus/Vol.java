package com.activity.platform.pojo.plus;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("volunteer")
public class Vol {
    private Long id;
    private Long userId;
    private Long activityId;
    private Long characterId;
    private Integer status; //0:未开始 1:已签到 2:未签到
}
