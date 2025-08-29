package com.activity.platform.util;

import java.util.HashMap;
import java.util.Map;


public class RedisTypeConverter {
    

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