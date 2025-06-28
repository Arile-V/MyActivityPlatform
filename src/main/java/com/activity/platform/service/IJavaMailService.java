package com.activity.platform.service;

import com.activity.platform.pojo.inner.Notification;
import jakarta.mail.MessagingException;

public interface IJavaMailService {
    /**
     * 发送验证码邮件
     *
     * @param email 收件人邮箱
     * @return 执行结果 0：失败1：成功
     * @throws MessagingException
     */
    Boolean sendEmailCode(String email,String code) throws MessagingException;

    /**
     * 发送下消息通知
     *
     * @param notification
     * @return
     * @throws MessagingException
     */
    Integer sendNotification(Notification notification) throws MessagingException;
}
