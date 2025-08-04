package com.activity.platform.service.impl;

import com.activity.platform.dto.Result;
import com.activity.platform.dto.UserDTO;
import com.activity.platform.mapper.VolMapper;
import com.activity.platform.pojo.plus.Vol;
import com.activity.platform.service.IVolService;
import com.activity.platform.util.SnowflakeIdWorker;
import com.activity.platform.util.UserHolder;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

@Service
public class VolServiceImpl extends ServiceImpl<VolMapper, Vol> implements IVolService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private SnowflakeIdWorker idWorker;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static{
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    BlockingQueue<Vol> queue = new ArrayBlockingQueue<>(1000);

    @PostConstruct
    public void init() {
        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    Vol successGuy = queue.take();
                    save(successGuy);
                    // 处理id
                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public Result get(Long characterId) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录");
        } else if (stringRedisTemplate.hasKey("user:lock:" + user.getId())) {
            return Result.fail("请勿重复提交");
        } else if (stringRedisTemplate.hasKey("user:vol:"+user.getId())) {
            return Result.fail("请勿重复提交");
        } else if (!stringRedisTemplate.hasKey("character:" + characterId)) {
            return Result.fail("角色不存在");
        }
        RLock lockUser = redissonClient.getLock("user:lock:" + user.getId());
        lockUser.lock();
        try{
            Long result = stringRedisTemplate.execute(
                    SECKILL_SCRIPT,
                    Collections.emptyList(),
                    user.getId().toString(),
                    characterId.toString()

            );
            if (result == 1) {
                return Result.fail("非法请求");
            }if(result == 2){
                return Result.fail("名额已满");
            }if(result == 3){
                return Result.fail("请勿重复报名");
            }
            Vol vol = new Vol();
            vol.setId(idWorker.nextId());
            vol.setUserId(user.getId());
            vol.setActivityId(characterId);
            vol.setStatus(0);
            queue.put(vol);
            return Result.ok(vol.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            lockUser.unlock();
        }
    }
}
