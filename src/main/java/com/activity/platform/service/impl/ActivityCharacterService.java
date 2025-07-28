package com.activity.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.activity.platform.dto.Result;
import com.activity.platform.mapper.ActivityCharacterMapper;
import com.activity.platform.pojo.ActivityCharacter;
import com.activity.platform.pojo.plus.Vol;
import com.activity.platform.service.IActivityCharacterService;
import com.activity.platform.service.IVolService;
import com.activity.platform.util.CacheUtil;
import com.activity.platform.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ActivityCharacterService extends ServiceImpl<ActivityCharacterMapper, ActivityCharacter> implements IActivityCharacterService{

    private final StringRedisTemplate stringRedisTemplate;
    private final CacheUtil cacheUtil;
    @Resource
    private IVolService volService;
    private final SnowflakeIdWorker idWorker;

    public ActivityCharacterService(StringRedisTemplate stringRedisTemplate, CacheUtil cacheUtil,
                                    SnowflakeIdWorker idWorker) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.cacheUtil = cacheUtil;
        this.idWorker = idWorker;
    }

    @Override
    @Transactional
    public Result create(ActivityCharacter activityCharacter) {
        Long id = idWorker.nextId();
        activityCharacter.setId(id);
        Map<String,Object> characterMap = BeanUtil.beanToMap(activityCharacter);
        stringRedisTemplate.opsForHash().putAll("seckill:character:"+activityCharacter.getId(),characterMap);
        save(activityCharacter);
        return Result.ok(id.toString());
    }

    @Override
    public Result delete(Long id) {
        stringRedisTemplate.delete("seckill:character:"+id);
        QueryChainWrapper<Vol> vol = volService.query().eq("activity_id",id);
        volService.remove(vol);
        remove(query().eq("id",id));
        return Result.ok();
    }

    @Override
    public Result update(ActivityCharacter activityCharacter) {
        stringRedisTemplate.opsForHash().putAll("seckill:character:"+activityCharacter.getId(),BeanUtil.beanToMap(activityCharacter));
        updateById(activityCharacter);
        return Result.ok();
    }

    @Override
    public Result queryByActivity(Long id) {
        return Result.ok(
                query().eq("activity_id",id).list()
        );
    }
}
