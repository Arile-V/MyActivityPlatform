package com.activity.platform.service.impl;

import cn.hutool.json.JSONUtil;
import com.activity.platform.dto.Result;
import com.activity.platform.dto.UserDTO;
import com.activity.platform.mapper.VolMapper;
import com.activity.platform.pojo.Activity;
import com.activity.platform.pojo.ActivityCharacter;
import com.activity.platform.pojo.plus.Vol;
import com.activity.platform.service.IActivityCharacterService;
import com.activity.platform.service.IActivityService;
import com.activity.platform.service.IVolService;
import com.activity.platform.util.SnowflakeIdWorker;
import com.activity.platform.util.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.catalina.core.ApplicationContext;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources.Chain.Strategy.Content;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.ContentHandler;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.activity.platform.util.RedisString.ACTIVITY;

@Service
public class VolServiceImpl extends ServiceImpl<VolMapper, Vol> implements IVolService {

    @Resource
    private final StringRedisTemplate stringRedisTemplate;

    @Resource
    private final RedissonClient redissonClient;

    @Resource
    private final SnowflakeIdWorker idWorker;

    @Resource
    @Lazy
    private final IActivityService activityService;

    @Resource
    @Lazy
    private final IActivityCharacterService activityCharacterService;

    private final org.springframework.context.ApplicationContext applicationContext;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static{
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    private static final DefaultRedisScript<Long> REMOVE_SCRIPT;
    static{
        REMOVE_SCRIPT = new DefaultRedisScript<>();
        REMOVE_SCRIPT.setLocation(new ClassPathResource("remove.lua"));
        REMOVE_SCRIPT.setResultType(Long.class);
    }

    BlockingQueue<Vol> queue = new ArrayBlockingQueue<>(1000);

    public VolServiceImpl(
            StringRedisTemplate stringRedisTemplate,
            RedissonClient redissonClient,
            SnowflakeIdWorker idWorker,
            IActivityService activityService,
            IActivityCharacterService activityCharacterService,
            org.springframework.context.ApplicationContext applicationContext) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redissonClient = redissonClient;
        this.idWorker = idWorker;
        this.activityService = activityService;
        this.activityCharacterService = activityCharacterService;
        this.applicationContext = applicationContext;}

    @PostConstruct
    public void init() {
        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    Vol successGuy = queue.take();
                    applicationContext
                            .getBean(this.getClass())
                            .saveVol(successGuy);
                    // 处理id
                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    @Transactional
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
            long result = stringRedisTemplate.execute(
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
            stringRedisTemplate.expire(
                    "vol:user"+user.getId()+":character:"+characterId,
                    Duration.between(
                            LocalDateTime.now(),
                            activityService
                                    .getById(characterId)
                                    .getEndToGetTime()
                                    .toLocalDateTime()
                    )
            );
            return Result.ok(vol.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            lockUser.unlock();
        }
    }
    @Transactional
    public void saveVol(Vol vol){
        save(vol);
    }

    @Override
    @Transactional
    public Result quit(Long characterId) {
        UserDTO user = UserHolder.getUser();
        LambdaQueryWrapper<Vol> volLambdaQueryWrapper = new LambdaQueryWrapper<>();
        volLambdaQueryWrapper.eq(Vol::getUserId, user.getId()).eq(Vol::getActivityId, characterId);
        Vol vol2Remove = getOne(volLambdaQueryWrapper);
        if (user == null) {
            return Result.fail("请先登录");
        }else if(!stringRedisTemplate.hasKey("vol:user"+user.getId()+":character:"+characterId)){
            return Result.fail("非法请求");
        }else if(vol2Remove == null){
            return Result.fail("请稍后再试");
        }
        Long result = stringRedisTemplate.execute(
                REMOVE_SCRIPT,
                Collections.emptyList(),
                user.getId().toString(),
                characterId.toString()
        );
        if(result == 0){
            return Result.fail("非法请求");
        }
        remove(volLambdaQueryWrapper);
        return Result.ok("退出成功");
    }

    @Override
    public Result check(Long characterId) {
        LambdaQueryWrapper<Vol> volLambdaQueryWrapper = new LambdaQueryWrapper<>();
        UserDTO user = UserHolder.getUser();
        volLambdaQueryWrapper.eq(Vol::getActivityId, characterId).eq(Vol::getUserId, user.getId());
        Vol vol2Check = getOne(volLambdaQueryWrapper);
        if(vol2Check == null){
            return Result.fail("非法请求");
        }
        else if(vol2Check.getStatus() == 0){
            return Result.fail("活动未开始");
        }
        else if(vol2Check.getStatus() == 1){
            return Result.fail("已签到");
        }
        vol2Check.setStatus(1);
        save(vol2Check);
        return Result.ok("签到成功");
    }

    @Override
    public Result checkUserSignUp(Long activityCharacterId) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录");
        }
        
        // 检查Redis中是否有报名记录
        boolean hasSignedUp = stringRedisTemplate.hasKey("vol:user" + user.getId() + ":character:" + activityCharacterId);
        
        // 同时检查数据库中的记录
        LambdaQueryWrapper<Vol> volLambdaQueryWrapper = new LambdaQueryWrapper<>();
        volLambdaQueryWrapper.eq(Vol::getUserId, user.getId()).eq(Vol::getActivityId, activityCharacterId);
        Vol volRecord = getOne(volLambdaQueryWrapper);
        
        boolean isSignedUp = hasSignedUp || volRecord != null;
        
        return Result.ok(isSignedUp);
    }

    @Override
    public Result lists() {
        UserDTO user = UserHolder.getUser();
        if(user == null){
            return Result.fail("请先登录");
        }
        LambdaQueryWrapper<Vol> volLambdaQueryWrapper = new LambdaQueryWrapper<>();
        volLambdaQueryWrapper.eq(Vol::getUserId, user.getId());
        List<Vol> volList = list(volLambdaQueryWrapper);
        if(volList.isEmpty()){
            return Result.ok("您还没有参与过活动");
        }
        LambdaQueryWrapper<Activity> activityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        activityLambdaQueryWrapper.in(Activity::getId, volList.stream().map(Vol::getActivityId).collect(Collectors.toList()));
        List<Activity> activityList = activityService.list(activityLambdaQueryWrapper);
        Map<String,List> resultMap = new HashMap<>();
        resultMap.put("volList",volList);
        resultMap.put("activityList",activityList);
        return Result.ok(resultMap);
    }

    @Override
    public Result info(Long characterId) {
        UserDTO user = UserHolder.getUser();
        LambdaQueryWrapper<Vol> volLambdaQueryWrapper = new LambdaQueryWrapper<>();
        volLambdaQueryWrapper.eq(Vol::getActivityId, characterId).eq(Vol::getUserId, user.getId());
        Vol vol2Check = getOne(volLambdaQueryWrapper);
        if(vol2Check == null){
            return Result.fail("非法请求");
        }
        LambdaQueryWrapper<ActivityCharacter> activityCharacterLambdaQueryWrapper = new LambdaQueryWrapper<>();
        activityCharacterLambdaQueryWrapper.eq(ActivityCharacter::getId, characterId);
        ActivityCharacter activityCharacter = activityCharacterService.getOne(activityCharacterLambdaQueryWrapper);
        LambdaQueryWrapper<Activity> activityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        activityLambdaQueryWrapper.eq(Activity::getId, activityCharacter.getActivityId());
        Activity activity = activityService.getOne(activityLambdaQueryWrapper);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("vol",vol2Check);
        resultMap.put("activity",activity);
        resultMap.put("activityCharacter",activityCharacter);
        return Result.ok(resultMap);
    }

  @Override
    public void start(Long activityId){
      LambdaQueryWrapper<Vol> volQuery = new LambdaQueryWrapper<Vol>();
      volQuery.select(Vol::getId,Vol::getStatus).eq(Vol::getActivityId,activityId);
      List<Vol> volList = list(volQuery);
      volList.stream().forEach(vol -> {
          vol.setStatus(2);
      });
      updateBatchById(volList);
  }

  @Override
    public void start(List<Long> activityIds){
      LambdaUpdateWrapper<Vol> volLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
      volLambdaUpdateWrapper.set(Vol::getStatus,2).in(Vol::getActivityId,activityIds);
      update(volLambdaUpdateWrapper);
  }

    @Override
    public void badVol(List<Long> activityIds) {
        if (activityIds==null||activityIds.isEmpty()){
            return;
        }
        LambdaUpdateWrapper<Vol> volLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        volLambdaUpdateWrapper.set(Vol::getStatus,2).in(Vol::getActivityId,activityIds);
        update(volLambdaUpdateWrapper);
    }
}
