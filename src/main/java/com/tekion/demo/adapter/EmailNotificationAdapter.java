package com.tekion.demo.adapter;

import com.tekion.demo.notification.Notification;
import com.tekion.demo.notification.NotificationResult;
import com.tekion.demo.notification.NotificationType;
import com.tekion.demo.port.NotificationPort;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationAdapter implements NotificationPort {

    @Override
    public NotificationResult send(Notification notification) {
        System.out.println("Sending EMAIL to: " + notification.getRecipient() +
                " | Message: " + notification.getMessage());
        return NotificationResult.builder()
                .success(true)
                .message("Email sent successfully")
                .build();
    }

    @Override
    public boolean supports(NotificationType type) {
        return type == NotificationType.EMAIL;
    }
}
