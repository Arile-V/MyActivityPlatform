package com.activity.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.activity.platform.pojo.inner.MyLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper extends BaseMapper<MyLog> {
}
