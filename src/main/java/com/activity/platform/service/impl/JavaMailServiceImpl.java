package com.activity.platform.service.impl;

import com.activity.platform.pojo.Notification;
import com.activity.platform.service.IJavaMailService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

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
    public Integer sendEmailCode(String email) throws MessagingException {
        return null;
    }

    @Override
    public Integer sendNotification(Notification notification) throws MessagingException {
        return null;
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }
}
