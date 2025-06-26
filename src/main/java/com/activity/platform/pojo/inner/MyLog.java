package com.activity.platform.pojo.inner;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_log")
public class MyLog {
    @TableId
    private Long id;
    private String ip;
    private String method;
    private String request;
    private String exception;
    private Timestamp time;
    private String result;
    private Integer status;
}
