package com.activity.platform.service;

import com.activity.platform.pojo.inner.Notification;
import jakarta.mail.MessagingException;

public interface IJavaMailService {

    Boolean sendEmailCode(String email,String code) throws MessagingException;


    Integer sendNotification(Notification notification) throws MessagingException;
}
