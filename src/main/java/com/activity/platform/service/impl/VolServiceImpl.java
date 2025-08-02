package com.activity.platform.service.impl;

import com.activity.platform.dto.Result;
import com.activity.platform.dto.UserDTO;
import com.activity.platform.mapper.VolMapper;
import com.activity.platform.pojo.plus.Vol;
import com.activity.platform.service.IVolService;
import com.activity.platform.util.UserHolder;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class VolServiceImpl extends ServiceImpl<VolMapper, Vol> implements IVolService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public Result get() {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录");
        } else if (stringRedisTemplate.hasKey("user:lock:" + user.getId())) {
            return Result.fail("请勿重复提交");
        } else if (stringRedisTemplate.hasKey("user:vol:"+user.getId())) {
            return Result.fail("请勿重复提交");
        }
        RLock lockUser = redissonClient.getLock("user:lock:" + user.getId());
        lockUser.lock();
        try{
            //
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            lockUser.unlock();
        }
        return null;
    }
}
