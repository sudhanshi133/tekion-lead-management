package com.tekion.demo.adapter;

import com.tekion.demo.TestDataBuilder;
import com.tekion.demo.notification.Notification;
import com.tekion.demo.notification.NotificationResult;
import com.tekion.demo.notification.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmsNotificationAdapterTest {

    private SmsNotificationAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SmsNotificationAdapter();
    }

    @Test
    void shouldSupportSmsNotificationType() {
        assertTrue(adapter.supports(NotificationType.SMS));
    }

    @Test
    void shouldNotSupportEmailNotificationType() {
        assertFalse(adapter.supports(NotificationType.EMAIL));
    }

    @Test
    void shouldSendSmsSuccessfully() {
        Notification notification = TestDataBuilder.createNotificationWithType(NotificationType.SMS);
        
        NotificationResult result = adapter.send(notification);
        
        assertTrue(result.isSuccess());
        assertEquals("SMS sent successfully", result.getMessage());
    }

    @Test
    void shouldHandleNotificationWithRecipient() {
        Notification notification = Notification.builder()
                .recipient("dealer456")
                .message("Test SMS")
                .type(NotificationType.SMS)
                .build();
        
        NotificationResult result = adapter.send(notification);
        
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldHandleNotificationWithMessage() {
        Notification notification = Notification.builder()
                .recipient("dealer789")
                .message("Urgent lead notification")
                .type(NotificationType.SMS)
                .build();
        
        NotificationResult result = adapter.send(notification);
        
        assertTrue(result.isSuccess());
    }
}

