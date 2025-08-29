package com.activity.platform.service.impl;

import cn.hutool.json.JSONUtil;
import com.activity.platform.dto.Result;
import com.activity.platform.dto.UserDTO;
import com.activity.platform.mapper.VolMapper;
import com.activity.platform.pojo.Activity;
import com.activity.platform.pojo.ActivityCharacter;
import com.activity.platform.pojo.User;
import com.activity.platform.pojo.plus.Vol;
import com.activity.platform.service.IActivityCharacterService;
import com.activity.platform.service.IActivityService;
import com.activity.platform.service.IVolService;
import com.activity.platform.service.IUserService;
import com.activity.platform.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
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
@Slf4j
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

    @Resource
    private IUserService userService; // 注入IUserService

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

    private final BlockingQueue<Vol> queue = new ArrayBlockingQueue<>(1000);

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
        this.applicationContext = applicationContext;
    }

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
    public Result get(Long characterId, Long activityId, String email) {
        if (email == null || email.trim().isEmpty()) {
            return Result.fail("用户邮箱不能为空");
        }
        
        // 通过邮箱查找用户ID
        Long userId = getUserIdByEmail(email);
        if (userId == null) {
            return Result.fail("用户不存在，请先注册");
        }
        
        // 检查角色是否存在（从数据库查询，不是缓存）
        ActivityCharacter character = activityCharacterService.getById(characterId);
        if (character == null) {
            return Result.fail("角色不存在");
        }
        
        // 检查角色是否已满
        Long signedUpCount = count(new LambdaQueryWrapper<Vol>().eq(Vol::getActivityId, activityId).eq(Vol::getCharacterId, characterId));
        if (signedUpCount >= character.getVolume()) {
            return Result.fail("该角色名额已满");
        }
        
        // 修改逻辑为每个用户只能报名一个角色
        LambdaQueryWrapper<Vol> existingVolQuery = new LambdaQueryWrapper<>();
        existingVolQuery.eq(Vol::getUserId, userId).eq(Vol::getActivityId, activityId);
        Vol existingVol = getOne(existingVolQuery);
        if (existingVol != null) {
            return Result.fail("您已经报名过这个活动的其他角色，每个用户只能报名一个角色");
        }
        
        // 直接保存到数据库
        try {
            Vol vol = new Vol();
            vol.setId(idWorker.nextId());
            vol.setUserId(userId);
            vol.setCharacterId(characterId);
            vol.setActivityId(activityId);
            vol.setStatus(0);
            
            // 保存报名记录到数据库
            save(vol);
            
            // 更新角色容量 -1
            try {
                // 使用原子操作更新容量，确保不会出现负数
                LambdaUpdateWrapper<ActivityCharacter> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ActivityCharacter::getId, characterId)
                           .ge(ActivityCharacter::getVolume, 1) // 确保容量大于等于1
                           .setSql("volume = volume - 1");
                boolean updateSuccess = activityCharacterService.update(updateWrapper);
                
                if (!updateSuccess) {
                    log.warn("更新角色容量失败: characterId={}, 可能容量不足", characterId);
                    // 如果更新失败，回滚报名记录
                    removeById(vol.getId());
                    return Result.fail("角色容量不足，报名失败");
                } else {
                    log.info("成功更新角色容量: characterId={}", characterId);
                }
            } catch (Exception e) {
                log.error("更新角色容量时发生异常: characterId={}", characterId, e);
                // 如果更新失败，回滚报名记录
                removeById(vol.getId());
                return Result.fail("报名失败，请重试");
            }
            
            return Result.ok(vol.getId());
        } catch (Exception e) {
            log.error("保存报名记录失败: characterId={}, userId={}", characterId, userId, e);
            return Result.fail("报名失败，请重试");
        }
    }
    
    // 通过邮箱查找用户ID
    private Long getUserIdByEmail(String email) {
        try {
            // URL解码邮箱，将%40转换为@
            String decodedEmail = java.net.URLDecoder.decode(email, "UTF-8");
            
            // 首先从Redis中查找用户信息
            String userJson = stringRedisTemplate.opsForValue().get("user:email:" + decodedEmail);
            if (userJson != null) {
                try {
                    // 解析用户信息获取ID
                    UserDTO user = JSONUtil.toBean(userJson, UserDTO.class);
                    if (user != null && user.getId() != null) {
                        return user.getId();
                    }
                } catch (Exception e) {
                    log.warn("Redis中用户数据格式错误，从数据库重新查询: {}", decodedEmail);
                }
            }
            
            // 如果Redis中没有或数据格式错误，从数据库查找
            try {
                // 通过邮箱查找用户 - 修复：直接传递解码后的邮箱字符串
                User user = userService.query().eq("email", decodedEmail).one();
                if (user != null) {
                    // 将用户信息缓存到Redis中，方便下次查询
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(user.getId());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setUsername(user.getUsername());
                    // 设置缓存过期时间为1小时
                    stringRedisTemplate.opsForValue().set("user:email:" + decodedEmail, JSONUtil.toJsonStr(userDTO), Duration.ofHours(1));
                    return user.getId();
                }
            } catch (Exception e) {
                log.error("从数据库查找用户失败: {}", decodedEmail, e);
            }
            
            return null;
        } catch (Exception e) {
            log.error("通过邮箱查找用户失败: {}", email, e);
            return null;
        }
    }

    @Override
    @Transactional
    public Result quit(Long characterId, String email) {
        if (email == null || email.trim().isEmpty()) {
            return Result.fail("用户邮箱不能为空");
        }
        
        // 通过邮箱查找用户ID
        Long userId = getUserIdByEmail(email);
        if (userId == null) {
            return Result.fail("用户不存在，请先注册");
        }
        
        LambdaQueryWrapper<Vol> volLambdaQueryWrapper = new LambdaQueryWrapper<>();
        volLambdaQueryWrapper.eq(Vol::getUserId, userId).eq(Vol::getActivityId, characterId);
        Vol vol2Remove = getOne(volLambdaQueryWrapper);

        if(!stringRedisTemplate.hasKey("vol:user"+userId+":character:"+characterId)){
            return Result.fail("非法请求");
        }else if(vol2Remove == null){
            return Result.fail("请稍后再试");
        }

        Long result = stringRedisTemplate.execute(
                REMOVE_SCRIPT,
                Collections.emptyList(),
                userId.toString(),
                characterId.toString()
        );
        if(result == 0){
            return Result.fail("非法请求");
        }
        // 直接从数据库删除报名记录
        try {
            remove(volLambdaQueryWrapper);
            
            // 恢复角色容量 +1
            try {
                LambdaUpdateWrapper<ActivityCharacter> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ActivityCharacter::getId, characterId)
                           .setSql("volume = volume + 1");
                boolean updateSuccess = activityCharacterService.update(updateWrapper);
                
                if (!updateSuccess) {
                    log.warn("恢复角色容量失败: characterId={}", characterId);
                    // 注意：这里不抛出异常，因为退出报名已经成功，只是容量恢复失败
                } else {
                    log.info("成功恢复角色容量: characterId={}", characterId);
                }
            } catch (Exception e) {
                log.error("恢复角色容量时发生异常: characterId={}", characterId, e);
                // 注意：这里不抛出异常，因为退出报名已经成功，只是容量恢复失败
            }
            
            return Result.ok("退出成功");
        } catch (Exception e) {
            log.error("退出报名失败: characterId={}, userId={}", characterId, userId, e);
            return Result.fail("退出失败，请重试");
        }
    }

    @Override
    public Result check(Long characterId, String email) {
        if (email == null || email.trim().isEmpty()) {
            return Result.fail("用户邮箱不能为空");
        }

        // 通过邮箱查找用户ID
        Long userId = getUserIdByEmail(email);
        if (userId == null) {
            return Result.fail("用户不存在，请先注册");
        }

        LambdaQueryWrapper<Vol> volLambdaQueryWrapper = new LambdaQueryWrapper<>();
        volLambdaQueryWrapper.eq(Vol::getActivityId, characterId).eq(Vol::getUserId, userId);
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

    public Result checkUserSignUp(Long activityCharacterId) {
        // 这个方法需要用户ID参数，但由于接口签名不变，我们暂时保留
        // 实际使用时应该传入userId参数
        return Result.fail("此方法需要重构，请使用新的接口");
    }

    // 新增方法：检查指定用户是否已报名指定活动角色
    @Override
    public Result checkUserSignUp(Long activityCharacterId, String email) {
        if (email == null || email.trim().isEmpty()) {
            return Result.fail("用户邮箱不能为空");
        }

        // 通过邮箱查找用户ID
        Long userId = getUserIdByEmail(email);
        if (userId == null) {
            return Result.fail("用户不存在，请先注册");
        }

        // 检查Redis中是否有报名记录
        boolean hasSignedUp = stringRedisTemplate.hasKey("vol:user" + userId + ":character:" + activityCharacterId);

        // 同时检查数据库中的记录
        LambdaQueryWrapper<Vol> volLambdaQueryWrapper = new LambdaQueryWrapper<>();
        volLambdaQueryWrapper.eq(Vol::getUserId, userId).eq(Vol::getActivityId, activityCharacterId);
        Vol volRecord = getOne(volLambdaQueryWrapper);

        boolean isSignedUp = hasSignedUp || volRecord != null;
        
        return Result.ok(isSignedUp);
    }

    @Override
    public Result lists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Result.fail("用户邮箱不能为空");
        }
        
        // 通过邮箱查找用户ID
        Long userId = getUserIdByEmail(email);
        if (userId == null) {
            return Result.fail("用户不存在，请先注册");
        }
        
        LambdaQueryWrapper<Vol> volLambdaQueryWrapper = new LambdaQueryWrapper<>();
        volLambdaQueryWrapper.eq(Vol::getUserId, userId);
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
        // 这个方法需要用户ID参数，但由于我们已经去掉了登录验证
        // 暂时返回错误信息，或者可以修改接口签名添加email参数
        return Result.fail("此接口需要用户身份验证，请使用其他接口");
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

    @Transactional
    public void saveVol(Vol vol){
        save(vol);
    }
}
