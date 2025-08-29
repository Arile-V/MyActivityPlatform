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
import com.activity.platform.service.IActivityCharacterService;
import com.activity.platform.util.CacheUtil;
import com.activity.platform.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashSet;

import static com.activity.platform.enums.ActivityStatus.END;
import static com.activity.platform.enums.ActivityStatus.START;
import static com.activity.platform.util.RedisString.ACTIVITY;
import static com.activity.platform.util.RedisString.ACTIVITY_HOT;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {
    
    private static final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Resource
    private final SnowflakeIdWorker idWorker;
    @Resource
    private final StringRedisTemplate stringRedisTemplate;
    @Resource
    private final CacheUtil cacheUtil;
    @Resource
    private final IActivityCharacterService activityCharacterService;


    public ActivityServiceImpl(
            SnowflakeIdWorker idWorker,
            StringRedisTemplate stringRedisTemplate,
            CacheUtil cacheUtil,
            IActivityCharacterService activityCharacterService) {
        this.idWorker = idWorker;
        this.stringRedisTemplate = stringRedisTemplate;
        this.cacheUtil = cacheUtil;
        this.activityCharacterService = activityCharacterService;
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
    @Transactional
    public Result createActivity(Activity activity, List<ActivityCharacter> characters) {
        try {
            // 创建活动
            Long activityId = idWorker.nextId();
            activity.setId(activityId);
            activity.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
            save(activity);
            
            // 创建活动角色
            if (characters != null && !characters.isEmpty()) {
                for (ActivityCharacter character : characters) {
                    character.setActivityId(activityId);
                    // 现在字段名已经正确映射，可以直接创建角色
                    activityCharacterService.create(character);
                    log.info("创建活动角色成功: {}", character);
                }
            }
            
            // 缓存活动信息
            cacheUtil.load(ACTIVITY + activityId, activity);
            
            return Result.ok(activityId);
        } catch (Exception e) {
            log.error("创建活动失败: ", e);
            return Result.fail("创建活动失败: " + e.getMessage());
        }
    }

    @Override
    public Result activityPage(Integer pageNum, Integer pageSize) {
        try {
            log.info("开始分页查询活动，页码: {}, 每页大小: {}", pageNum, pageSize);
            
            // 先查询总数
            long total = count();
            log.info("活动总数: {}", total);
            
            if (total == 0) {
                log.warn("数据库中没有活动数据");
                return Result.ok(new Page<Activity>(pageNum, pageSize));
            }
            
            // 使用MyBatis-Plus的分页功能
            Page<Activity> page = new Page<>(pageNum, pageSize);
            Page<Activity> result = page(page);
            
            log.info("分页查询成功，返回记录数: {}, 总页数: {}", 
                result.getRecords().size(), result.getPages());
            
            return Result.ok(result);
        } catch (Exception e) {
            log.error("分页查询活动失败: ", e);
            return Result.fail("分页查询失败: " + e.getMessage());
        }
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
    @Transactional
    public Result deleteActivity(Long activityId) {
        try {
            // 检查活动是否存在
            Activity activity = getById(activityId);
            if (activity == null) {
                return Result.fail("活动不存在");
            }
            
            // 删除活动角色（如果有的话）
            try {
                LambdaQueryWrapper<ActivityCharacter> characterQuery = new LambdaQueryWrapper<>();
                characterQuery.eq(ActivityCharacter::getActivityId, activityId);
                List<ActivityCharacter> characters = activityCharacterService.list(characterQuery);
                
                if (!characters.isEmpty()) {
                    // 删除所有相关的活动角色
                    for (ActivityCharacter character : characters) {
                        activityCharacterService.delete(character.getId());
                    }
                    log.info("删除了活动 {} 的 {} 个角色", activityId, characters.size());
                }
            } catch (Exception e) {
                log.warn("删除活动角色时发生异常，继续删除活动: {}", e.getMessage());
            }
            
            // 删除活动
            boolean removed = removeById(activityId);
            if (!removed) {
                return Result.fail("删除活动失败");
            }
            
            // 从热门列表中移除
            removeFromHotList(activityId);
            
            // 清理Redis缓存
            stringRedisTemplate.delete(ACTIVITY + activityId);
            
            log.info("活动 {} 删除成功", activityId);
            return Result.ok("活动删除成功");
            
        } catch (Exception e) {
            log.error("删除活动 {} 时发生异常: ", activityId, e);
            return Result.fail("删除活动失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void start(Long activityId) {
        //将这个ID的活动及其角色改变为激活状态，相关报名全部转为未签到状态等待签到
        Activity activity = getById(activityId);
        activity.setStatus(String.valueOf(START));
        updateById(activity);
        // 使用CacheUtil更新缓存
        cacheUtil.load(ACTIVITY + activityId, activity);
    }
    @Transactional
    @Override
    public void start(List<Long> activityIds) {
        LambdaUpdateWrapper<Activity> activityLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        activityLambdaUpdateWrapper.set(Activity::getStatus,START).in(Activity::getId,activityIds);
        update(activityLambdaUpdateWrapper);
        
        activityIds.stream().forEach(activityId -> {
            Activity activity = getById(activityId);
            if (activity != null) {
                activity.setStatus(String.valueOf(START));
                cacheUtil.load(ACTIVITY + activityId, activity);
            }
        });
    }

    @Override
    @Transactional
    public void end(Long activityId) {
        //将这个ID的活动及其角色改变为结束状态
        Activity activity = getById(activityId);
        activity.setStatus(String.valueOf(END));
        updateById(activity);
        // 从热门列表中移除
        removeFromHotList(activityId);
        // 清理缓存
        stringRedisTemplate.delete(ACTIVITY + activityId);
    }

    @Override
    public Result getAllActivities() {
        try {
            List<Activity> activities = list();
            return Result.ok(activities);
        } catch (Exception e) {
            log.error("获取所有活动失败: ", e);
            return Result.fail("获取活动列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result setHotActivity(Long activityId) {
        try {
            // 检查活动是否存在
            Activity activity = getById(activityId);
            if (activity == null) {
                return Result.fail("活动不存在");
            }
            
            // 检查活动是否已过期
            if (activity.getEndTime() != null && activity.getEndTime().before(Timestamp.valueOf(LocalDateTime.now()))) {
                return Result.fail("活动已过期，不能设置为热点");
            }
            
            // 添加到热点列表，设置初始分数为1
            stringRedisTemplate.opsForZSet().add(ACTIVITY_HOT, ACTIVITY + activityId, 1.0);
            
            log.info("活动 {} 已设置为热点活动", activityId);
            return Result.ok("活动已设置为热点活动");
        } catch (Exception e) {
            log.error("设置热点活动失败: ", e);
            return Result.fail("设置热点活动失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result removeHotActivity(Long activityId) {
        try {
            // 从热点列表中移除
            Long removed = stringRedisTemplate.opsForZSet().remove(ACTIVITY_HOT, ACTIVITY + activityId);
            
            if (removed != null && removed > 0) {
                log.info("活动 {} 已从热点列表中移除", activityId);
                return Result.ok("活动已从热点列表中移除");
            } else {
                return Result.fail("活动不在热点列表中");
            }
        } catch (Exception e) {
            log.error("移除热点活动失败: ", e);
            return Result.fail("移除热点活动失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result getAllActivitiesForHotManage() {
        try {
            // 获取所有活动
            List<Activity> activities = list();
            
            // 获取当前热点活动ID列表
            Set<String> hotActivityKeys = stringRedisTemplate.opsForZSet().range(ACTIVITY_HOT, 0, -1);
            Set<Long> hotActivityIds = new HashSet<>();
            
            if (hotActivityKeys != null) {
                for (String key : hotActivityKeys) {
                    if (key.startsWith(ACTIVITY)) {
                        String idStr = key.substring(ACTIVITY.length());
                        try {
                            hotActivityIds.add(Long.parseLong(idStr));
                        } catch (NumberFormatException e) {
                            log.warn("无法解析活动ID: {}", idStr);
                        }
                    }
                }
            }
            
            // 为每个活动添加热点状态
            for (Activity activity : activities) {
                activity.setIsHot(hotActivityIds.contains(activity.getId()));
            }
            
            return Result.ok(activities);
        } catch (Exception e) {
            log.error("获取活动列表失败: ", e);
            return Result.fail("获取活动列表失败: " + e.getMessage());
        }
    }

}
