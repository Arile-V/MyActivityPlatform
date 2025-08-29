# Redis类型转换问题修复说明

## 问题描述

在 `ActivityCharacterService.create()` 方法中出现了以下异常：

```
java.lang.ClassCastException: class java.lang.Long cannot be cast to class java.lang.String 
(java.lang.Long and java.lang.String are in module java.base of loader 'bootstrap')
```

## 问题原因

1. **BeanUtil.beanToMap() 的类型问题**：
   - `BeanUtil.beanToMap(activityCharacter)` 返回的 Map 中，所有值都是 Object 类型
   - 对于 `ActivityCharacter` 对象，`id` 和 `activityId` 字段是 `Long` 类型
   - `volume` 字段是 `Integer` 类型

2. **Redis Hash 操作的类型要求**：
   - `stringRedisTemplate.opsForHash().putAll()` 要求所有的值都必须是 String 类型
   - 当尝试存储 Long 或 Integer 类型的值时，Redis 会抛出类型转换异常

3. **具体错误位置**：
   ```java
   // 问题代码
   Map<String,Object> characterMap = BeanUtil.beanToMap(activityCharacter);
   stringRedisTemplate.opsForHash().putAll(SECKILL_CHARACTER+activityCharacter.getId(),characterMap);
   ```

## 解决方案

### 1. 创建工具类 `RedisTypeConverter`

创建了专门的工具类来处理 Redis 类型转换：

```java
public class RedisTypeConverter {
    
    /**
     * 将Map<String,Object>转换为Map<String,String>，避免Redis类型转换异常
     */
    public static Map<String, String> convertToStringMap(Map<String, Object> objMap) {
        if (objMap == null) {
            return new HashMap<>();
        }
        
        Map<String, String> stringMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : objMap.entrySet()) {
            if (entry.getValue() != null) {
                stringMap.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return stringMap;
    }
}
```

### 2. 修复相关方法

#### ActivityCharacterService.create()
```java
@Override
@Transactional
public Result create(ActivityCharacter activityCharacter) {
    Long id = idWorker.nextId();
    activityCharacter.setId(id);
    Map<String,Object> characterMap = BeanUtil.beanToMap(activityCharacter);
    Map<String,String> stringMap = RedisTypeConverter.convertToStringMap(characterMap);
    stringRedisTemplate.opsForHash().putAll(SECKILL_CHARACTER+activityCharacter.getId(), stringMap);
    // ... 其他代码
}
```

#### ActivityCharacterService.update()
```java
@Override
@Transactional
public Result update(ActivityCharacter activityCharacter) {
    // ... 验证代码
    
    Map<String,Object> characterMap = BeanUtil.beanToMap(activityCharacter);
    Map<String,String> stringMap = RedisTypeConverter.convertToStringMap(characterMap);
    stringRedisTemplate.opsForHash().putAll(SECKILL_CHARACTER+activityCharacter.getId(), stringMap);
    // ... 其他代码
}
```

#### CacheUtil.load4Hash()
```java
public void load4Hash(String key, Object object){
    Map<String,Object> objMap = BeanUtil.beanToMap(object);
    Map<String,String> stringMap = RedisTypeConverter.convertToStringMap(objMap);
    stringRedisTemplate.opsForHash().putAll(key, stringMap);
}
```

## 修复效果

1. **解决了类型转换异常**：所有值在存储到 Redis 之前都被转换为 String 类型
2. **提高了代码复用性**：创建了通用的工具类，避免代码重复
3. **增强了代码健壮性**：处理了 null 值，避免了潜在的空指针异常
4. **保持了数据完整性**：所有数据都能正确存储到 Redis 中

## 使用建议

1. **在 Redis Hash 操作前**：始终使用 `RedisTypeConverter.convertToStringMap()` 进行类型转换
2. **处理 BeanUtil.beanToMap() 结果时**：注意返回的 Map 中值类型都是 Object，需要根据具体使用场景进行类型转换
3. **Redis 操作类型安全**：确保所有存储到 Redis 的值都是 String 类型

## 测试验证

创建了 `RedisTypeConverterTest` 测试类来验证修复效果，包括：
- 基本类型转换测试
- null 值处理测试
- 空 Map 处理测试
- ActivityCharacter 对象转换测试

## 注意事项

1. **性能影响**：类型转换会带来轻微的性能开销，但相比异常处理的开销可以忽略
2. **数据精度**：Long 类型转换为 String 时，大数值的精度不会丢失
3. **向后兼容**：修复后的代码完全向后兼容，不会影响现有功能 