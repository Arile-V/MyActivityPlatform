package com.activity.platform.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Component
public class CacheUtil { //用于制作页面缓存等静态缓存，采用逻辑过期
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final long time = 1000 * 60 * 60;
    public CacheUtil(StringRedisTemplate stringRedisTemplate, RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.stringRedisTemplate = stringRedisTemplate;
    }
    //逻辑过期缓存，在json或者hash缓存当中加上时间序列来对比和更新
    public void load(String key, Object object){
        Map<String,Object> objMap = BeanUtil.beanToMap(object);
        if(object.getClass() == String.class && ((String)object).equals("空对象")){
        }else{
            objMap.put("expire", System.currentTimeMillis()+time+ RandomUtil.randomLong(0L,600000L));//TODO 过期时间
        }
        String json = JSONUtil.toJsonStr(objMap);
        stringRedisTemplate.opsForValue().set(key, json);
    }

    public void load4Hash(String key, Object object){
        Map<String,Object> objMap = BeanUtil.beanToMap(object);
        Map<String,String> stringMap = RedisTypeConverter.convertToStringMap(objMap);
        stringRedisTemplate.opsForHash().putAll(key, stringMap);
    }

    /**
     * 将Map<String,Object>转换为Map<String,String>，避免Redis类型转换异常
     * @deprecated 使用 RedisTypeConverter.convertToStringMap() 替代
     */
    @Deprecated
    private Map<String,String> convertToStringMap(Map<String,Object> objMap) {
        return RedisTypeConverter.convertToStringMap(objMap);
    }

    public <T> T getHash(String key, Class<T> clazz) throws NoSuchFieldException, IllegalAccessException {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        T data = BeanUtil.mapToBean(entries, clazz, true);
        return data;
    }
    public <T> T getOrExpire(String key, Class<T> clazz, Function<Long,T> queryFunction) throws NoSuchFieldException, IllegalAccessException {
        String isEmpty = stringRedisTemplate.opsForValue().get("empty:"+key);
        if(isEmpty != null && isEmpty.equals("空对象")){
            return null;
        }
        String json = stringRedisTemplate.opsForValue().get(key);
        JSON json1 = JSONUtil.parse(json);
        LocalDateTime expire = (LocalDateTime)JSONUtil.getByPath(json1, "expire");
        T data = JSONUtil.toBean(json, clazz);
        if(expire.isBefore(LocalDateTime.now())){
            RLock lock = redissonClient.getLock("lock:"+key);

            if(lock.tryLock()){
                try {
                    data = queryFunction.apply((data.getClass().getField("id").getLong(data)));
                    if (data != null) {
                        T finalData = data;
                        Thread.ofVirtual().start(
                                () -> {
                                    try {
                                        load(key, finalData);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    } finally {
                                        lock.unlock();
                                    }
                                }
                        );
                    } else {
                        Thread.ofVirtual().start(() -> {
                            try{
                                load("empty:"+key,"不存在");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }finally {
                                lock.unlock();
                            }
                        });
                    }
                    return data;
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
        }
        return data;
    }
}
