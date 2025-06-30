package com.activity.platform.util;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SnowflakeIdWorker {
    @Value("${snowflake.worker-id:0}")
    private Long workerId;

    @Value("${snowflake.data-center-id:0}")
    private Long dataCenterId;

    private Snowflake snowflake;

    @PostConstruct
    public void init() {
        snowflake = IdUtil.getSnowflake(workerId, dataCenterId);
        log.info("初始化雪花算法，workerId: {}, dataCenterId: {}", workerId, dataCenterId);
    }

    public long nextId() {
        return snowflake.nextId();
    }

    public String nextIdStr() {
        return snowflake.nextIdStr();
    }

}
