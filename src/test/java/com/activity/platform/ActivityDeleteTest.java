package com.activity.platform;

import com.activity.platform.dto.ActivityDeleteRequest;
import com.activity.platform.pojo.Activity;
import com.activity.platform.service.IActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivityDeleteTest {

    @Resource
    private IActivityService activityService;

    @Test
    @Transactional
    @Rollback
    void testDeleteActivity() {
        // 测试删除不存在的活动
        ActivityDeleteRequest request = new ActivityDeleteRequest();
        request.setId(999999999L);
        
        var result = activityService.deleteActivity(request.getId());
        assertFalse(result.getSuccess());
        assertEquals("活动不存在", result.getErrorMsg());
    }
    
    @Test
    void testActivityDeleteRequest() {
        ActivityDeleteRequest request = new ActivityDeleteRequest();
        request.setId(12345L);
        
        assertEquals(12345L, request.getId());
        assertNotNull(request.toString());
    }
} 