package com.activity.platform.service.impl;

import com.activity.platform.service.ILogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.activity.platform.mapper.LogMapper;
import com.activity.platform.pojo.inner.MyLog;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, MyLog> implements ILogService {

}
