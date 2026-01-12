package com.tekion.demo.port;


import com.tekion.demo.notification.Notification;
import com.tekion.demo.notification.NotificationResult;
import com.tekion.demo.notification.NotificationType;

public interface NotificationPort {

    NotificationResult send(Notification notification);

    boolean supports(NotificationType type);
}