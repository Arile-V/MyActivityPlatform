package com.activity.platform;

import com.activity.platform.pojo.ActivityCharacter;
import com.activity.platform.util.RedisTypeConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisTypeConverterTest {

    @Test
    void testConvertToStringMap() {
        // 创建测试数据
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("id", 12345L);
        objMap.put("activityId", 67890L);
        objMap.put("name", "测试角色");
        objMap.put("volume", 100);
        objMap.put("nullValue", null);
        
        // 执行转换
        Map<String, String> result = RedisTypeConverter.convertToStringMap(objMap);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("12345", result.get("id"));
        assertEquals("67890", result.get("activityId"));
        assertEquals("测试角色", result.get("name"));
        assertEquals("100", result.get("volume"));
        assertFalse(result.containsKey("nullValue")); // null值应该被过滤掉
        
        // 验证所有值都是String类型
        for (String value : result.values()) {
            assertTrue(value instanceof String);
        }
    }
    
    @Test
    void testConvertToStringMapWithNullInput() {
        Map<String, String> result = RedisTypeConverter.convertToStringMap(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testConvertToStringMapWithEmptyInput() {
        Map<String, Object> emptyMap = new HashMap<>();
        Map<String, String> result = RedisTypeConverter.convertToStringMap(emptyMap);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testConvertToStringMapWithActivityCharacter() {
        // 模拟BeanUtil.beanToMap的结果
        Map<String, Object> characterMap = new HashMap<>();
        characterMap.put("id", 1961427622420418560L);
        characterMap.put("activityId", 1961427610575704064L);
        characterMap.put("name", "121231");
        characterMap.put("volume", 4);
        
        // 执行转换
        Map<String, String> result = RedisTypeConverter.convertToStringMap(characterMap);
        
        // 验证结果
        assertEquals("1961427622420418560", result.get("id"));
        assertEquals("1961427610575704064", result.get("activityId"));
        assertEquals("121231", result.get("name"));
        assertEquals("4", result.get("volume"));
        
        // 验证所有值都是String类型
        for (String value : result.values()) {
            assertTrue(value instanceof String);
        }
    }
} 