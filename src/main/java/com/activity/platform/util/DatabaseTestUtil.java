package com.activity.platform.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DatabaseTestUtil {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostConstruct
    public void testDatabaseConnection() {
        log.info("=== 开始测试数据库连接 ===");
        
        try {
            // 测试基本连接
            String result = jdbcTemplate.queryForObject("SELECT 'Database connected successfully!' as message", String.class);
            log.info("数据库连接测试: {}", result);
            
            // 检查表是否存在
            checkTables();
            
            // 检查管理员数据
            checkAdminData();
            
        } catch (Exception e) {
            log.error("数据库连接测试失败: {}", e.getMessage(), e);
        }
    }
    
    private void checkTables() {
        try {
            String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'activity'";
            List<String> tables = jdbcTemplate.queryForList(sql, String.class);
            
            log.info("当前数据库中的表: {}", tables);
            
            // 检查关键表是否存在
            boolean hasAdminTable = tables.contains("tb_admin");
            boolean hasUserTable = tables.contains("tb_user");
            boolean hasActivityTable = tables.contains("tb_activity");
            
            log.info("关键表检查结果:");
            log.info("  - tb_admin: {}", hasAdminTable ? "存在" : "不存在");
            log.info("  - tb_user: {}", hasUserTable ? "存在" : "不存在");
            log.info("  - tb_activity: {}", hasActivityTable ? "存在" : "不存在");
            
        } catch (Exception e) {
            log.error("检查表结构失败: {}", e.getMessage());
        }
    }
    
    private void checkAdminData() {
        try {
            String sql = "SELECT COUNT(*) as count FROM tb_admin";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            
            log.info("管理员账号数量: {}", count);
            
            if (count > 0) {
                String adminSql = "SELECT username, password FROM tb_admin LIMIT 5";
                List<Map<String, Object>> admins = jdbcTemplate.queryForList(adminSql);
                
                log.info("管理员账号列表:");
                for (Map<String, Object> admin : admins) {
                    log.info("  - 用户名: {}, 密码: {}", admin.get("username"), admin.get("password"));
                }
            }
            
        } catch (Exception e) {
            log.error("检查管理员数据失败: {}", e.getMessage());
        }
    }
} 