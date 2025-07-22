package com.activity.platform.service.impl;

import cn.hutool.json.JSONUtil;
import com.activity.platform.dto.Result;
import com.activity.platform.mapper.ActivityMapper;
import com.activity.platform.pojo.Activity;
import com.activity.platform.service.IActivityService;
import com.activity.platform.util.CacheUtil;
import com.activity.platform.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.activity.platform.util.RedisString.ACTIVITY;
import static com.activity.platform.util.RedisString.ACTIVITY_HOT;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {
    private final SnowflakeIdWorker idWorker;
    private final StringRedisTemplate stringRedisTemplate;
    private final CacheUtil cacheUtil;

    public ActivityServiceImpl(SnowflakeIdWorker idWorker, StringRedisTemplate stringRedisTemplate, CacheUtil cacheUtil) {
        this.idWorker = idWorker;
        this.stringRedisTemplate = stringRedisTemplate;
        this.cacheUtil = cacheUtil;
    }

    @Override
    @Transactional
    public Result createActivity(Activity activity) {
        Long activityId = idWorker.nextId();
        activity.setId(activityId);
        activity.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        save(activity);
        cacheUtil.load(ACTIVITY + activityId, activity);
        return Result.ok(activityId);
    }

    @Override
    public Result activityPage(Integer pageNum, Integer pageSize) {
        int number = count();
        List<Activity> activities = query().list().subList(number-(pageNum*pageSize),number-(pageNum-1)*pageNum);
        return Result.ok(activities);
    }

    @Override
    // 重写hotActivity方法
    public Result hotActivity() {
        // 从Redis中获取名为"activity:hot"的有序集合，并按降序获取前5个元素,zSet当中元素是活动缓存的key
        Set<String> set = stringRedisTemplate.opsForZSet().reverseRange(ACTIVITY_HOT, 0, 5);
        // 返回结果
        return Result.ok(set);
    }

// 从热门列表中移除指定活动
    private boolean removeFromHotList(Long activityId){
        // 如果移除活动成功，则返回false，否则返回true
        return Boolean.FALSE
                .equals(
                        // 比较移除活动后的返回值是否为0
                        Objects.equals(stringRedisTemplate.opsForZSet()
                        .remove(ACTIVITY_HOT, ACTIVITY + activityId), 0L)
                );
    }

    private boolean addOrPlusScore(Long activityID){
        // 判断是否将活动添加到Redis的有序集合中
        if(Boolean.TRUE
                .equals(stringRedisTemplate.opsForZSet()
                        .addIfAbsent(ACTIVITY_HOT, ACTIVITY + activityID, 0D))){
            // 如果添加成功，返回true
            return Boolean.TRUE;
        }else {
            // 如果添加失败，判断是否将活动的分数加1
            return Boolean.TRUE
                    .equals(
                            Objects.equals(stringRedisTemplate.opsForZSet()
                            .incrementScore(ACTIVITY_HOT, ACTIVITY + activityID, 1D), 0D)
                    );
        }
    }

    @Override
    public Activity getActivityById(Long activityId) throws NoSuchFieldException, IllegalAccessException {
        String json = stringRedisTemplate.opsForValue().get(ACTIVITY + activityId);
        if (json == null || json.isBlank()) {
            Activity activity = getById(activityId);
            Timestamp endTime = activity.getEndTime();
            if(endTime.before(Timestamp.valueOf(LocalDateTime.now()))){
                removeFromHotList(activityId);
                //过时活动不入缓存
                return activity;
            }
            addOrPlusScore(activityId);
            cacheUtil.load(ACTIVITY + activityId, activity);
            return activity;
        }else{
            addOrPlusScore(activityId);
            return cacheUtil
                    .getOrExpire(ACTIVITY + activityId, Activity.class, this::getById);
        }
    }

    @Override
    public Result updateActivity(Activity activity) {
        updateById(activity);
        cacheUtil.load(ACTIVITY + activity.getId(), activity);
        return Result.ok();
    }

    @Override
    public Result deleteActivity(Long activityId) {
        removeById(activityId);
        removeFromHotList(activityId);
        stringRedisTemplate.delete(ACTIVITY + activityId);
        return Result.ok();
    }
}
