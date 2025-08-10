package com.activity.platform.service.impl;

import cn.hutool.json.JSONUtil;
import com.activity.platform.dto.Result;
import com.activity.platform.enums.ActivityStatus;
import com.activity.platform.mapper.ActivityMapper;
import com.activity.platform.pojo.Activity;
import com.activity.platform.pojo.ActivityCharacter;
import com.activity.platform.pojo.plus.Vol;
import com.activity.platform.service.IActivityService;
import com.activity.platform.service.IVolService;
import com.activity.platform.util.CacheUtil;
import com.activity.platform.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
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

import static com.activity.platform.enums.ActivityStatus.END;
import static com.activity.platform.enums.ActivityStatus.START;
import static com.activity.platform.util.RedisString.ACTIVITY;
import static com.activity.platform.util.RedisString.ACTIVITY_HOT;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {
    @Resource
    private final SnowflakeIdWorker idWorker;
    @Resource
    private final StringRedisTemplate stringRedisTemplate;
    @Resource
    private final CacheUtil cacheUtil;


    public ActivityServiceImpl(
            SnowflakeIdWorker idWorker,
            StringRedisTemplate stringRedisTemplate,
            CacheUtil cacheUtil) {
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

    @Override
    @Transactional
    public void start(Long activityId) {
        //将这个ID的活动及其角色改变为激活状态，相关报名全部转为未签到状态等待签到
        Activity activity = getById(activityId);
        activity.setStatus(String.valueOf(START));
//        LambdaQueryWrapper<Vol> volQuery = new LambdaQueryWrapper<Vol>();
//        volQuery.select(Vol::getId,Vol::getStatus).eq(Vol::getActivityId,activityId);
//        List<Vol> volList = volService.list(volQuery);
//        volList.stream().forEach(vol -> {
//            vol.setStatus(2);
//        });
//        volService.updateBatchById(volList);
        updateById(activity);
        stringRedisTemplate.opsForValue().set(ACTIVITY + activityId, JSONUtil.toJsonStr(activity));
    }
    @Transactional
    @Override
    public void start(List<Long> activityIds) {
        LambdaUpdateWrapper<Activity> activityLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        activityLambdaUpdateWrapper.set(Activity::getStatus,START).in(Activity::getId,activityIds);
        update(activityLambdaUpdateWrapper);
//        LambdaUpdateWrapper<Vol> volLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//        volLambdaUpdateWrapper.set(Vol::getStatus,2).in(Vol::getActivityId,activityIds);
//        volService.update(volLambdaUpdateWrapper);
        activityIds.stream().forEach(activityId -> {
            Activity activity = JSONUtil.toBean(stringRedisTemplate.opsForValue().get(ACTIVITY + activityId), Activity.class);
            activity.setStatus(String.valueOf(START));
            cacheUtil.load(ACTIVITY + activityId,activity);
            //stringRedisTemplate.opsForValue().set(ACTIVITY + activityId, JSONUtil.toJsonStr(activity));
        });
    }

}
