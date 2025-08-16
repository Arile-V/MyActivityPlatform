package com.activity.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.activity.platform.dto.Result;
import com.activity.platform.mapper.ActivityCharacterMapper;
import com.activity.platform.pojo.ActivityCharacter;
import com.activity.platform.pojo.plus.Vol;
import com.activity.platform.service.IActivityCharacterService;
import com.activity.platform.service.IVolService;
import com.activity.platform.util.CacheUtil;
import com.activity.platform.util.RedisString;
import com.activity.platform.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.activity.platform.util.RedisString.ACTIVITY_ID;
import static com.activity.platform.util.RedisString.SECKILL_CHARACTER;
import static com.activity.platform.util.RedisString.VOL_CHARACTER;
import static com.activity.platform.util.RedisString.VOL_TO_DELETE;

@Service
public class ActivityCharacterService extends ServiceImpl<ActivityCharacterMapper, ActivityCharacter> implements IActivityCharacterService{

    @Resource
    private final  StringRedisTemplate stringRedisTemplate;
    @Resource
    private final  CacheUtil cacheUtil;
    @Resource
    private final SnowflakeIdWorker idWorker;

    public ActivityCharacterService(
            StringRedisTemplate stringRedisTemplate,
            CacheUtil cacheUtil,
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
        stringRedisTemplate.opsForHash().putAll(SECKILL_CHARACTER+activityCharacter.getId(),characterMap);
        //报名存放处
        stringRedisTemplate.opsForZSet().addIfAbsent(VOL_CHARACTER+activityCharacter.getId(),"tail",0);
        save(activityCharacter);
        return Result.ok(id.toString());
    }

    @Override
    @Transactional
    public Result delete(Long id) {
        //丢剩余票数
        stringRedisTemplate.delete(SECKILL_CHARACTER +id);
        //移除已经存在的报名
        stringRedisTemplate.delete(VOL_CHARACTER +id);
        remove(query().eq("id",id));
        return Result.ok();
    }

    @Override
    @Transactional
    public Result update(ActivityCharacter activityCharacter) {
        if (activityCharacter.getActivityId()==null){
            return Result.fail("活动ID不能为空");
        }
        stringRedisTemplate.opsForHash().putAll(SECKILL_CHARACTER+activityCharacter.getId(),BeanUtil.beanToMap(activityCharacter));
        updateById(activityCharacter);
        return Result.ok();
    }

    @Override
    //用于活动页面上展示所有可报名的项目
    public Result queryByActivity(Long id) {
        QueryWrapper<ActivityCharacter> wrapper = new QueryWrapper<ActivityCharacter>().eq(ACTIVITY_ID,id);
        return Result.ok(
                list(wrapper)
        );
    }
}
