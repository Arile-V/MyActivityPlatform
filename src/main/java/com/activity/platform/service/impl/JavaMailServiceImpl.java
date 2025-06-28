package com.activity.platform.service.impl;

import com.activity.platform.pojo.inner.Notification;
import com.activity.platform.service.IJavaMailService;
import com.activity.platform.util.EmailCode;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JavaMailServiceImpl implements IJavaMailService {
    @Resource
    private final StringRedisTemplate stringRedisTemplate;
    @Resource
    private final JavaMailSender javaMailSender;
    public JavaMailServiceImpl(StringRedisTemplate stringRedisTemplate,
                               JavaMailSender javaMailSender) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public Boolean sendEmailCode(String to,String code) throws MessagingException {
        if(Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                .setIfAbsent(
                        "email:tryLogin:wait:" + to, String.valueOf(System.currentTimeMillis()), 1, TimeUnit.MINUTES))){
            sendEmail(to, "验证码", code);
            //只接受最新验证码
            stringRedisTemplate.opsForValue().set("email:tryLogin:"+to, code, 5, TimeUnit.MINUTES);
            return true;
        } else{
            return false;
        }
    }

    @Override
    public Integer sendNotification(Notification notification) throws MessagingException {
        return null;
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("arileverlika@163.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }
}
