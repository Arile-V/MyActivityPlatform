package com.activity.platform;

import com.activity.platform.pojo.User;
import com.activity.platform.service.IJavaMailService;
import com.activity.platform.util.CacheUtil;
import com.activity.platform.util.EmailCode;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"mybatis-plus.mapper-locations=classpath:mapper/*.xml","mybatis-plus.type-aliases-package=com.activity.platform.pojo"})
@MapperScan("com.activity.platform.mapper")
class PlatformApplicationTests {

    @Test
    void contextLoads() {
    }

//    @Test
//    void testCache(){
//        User user = new User();
//        user.setId(1L);
//        user.setName("test");
//        CacheUtil.load(user);
//    }
    @Resource
    private IJavaMailService javaMailService;
    @Test
    void testMail() throws MessagingException {
        javaMailService.sendEmailCode("arileverlika@outlook.com", EmailCode.randomCode());
    }


}
