package com.activity.platform.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis类型转换工具类
 * 用于解决BeanUtil.beanToMap()返回的Map中值类型与Redis要求不匹配的问题
 */
public class RedisTypeConverter {
    
    /**
     * 将Map<String,Object>转换为Map<String,String>，避免Redis类型转换异常
     * 主要用于Redis Hash操作前的类型转换
     * 
     * @param objMap 原始Map，值类型为Object
     * @return 转换后的Map，值类型为String
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
    
    /**
     * 将Map<String,Object>转换为Map<String,String>，并过滤掉null值
     * 
     * @param objMap 原始Map，值类型为Object
     * @return 转换后的Map，值类型为String，不包含null值
     */
    public static Map<String, String> convertToStringMapFilterNull(Map<String, Object> objMap) {
        if (objMap == null) {
            return new HashMap<>();
        }
        
        Map<String, String> stringMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : objMap.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                stringMap.put(entry.getKey(), value.toString());
            }
        }
        return stringMap;
    }
} 