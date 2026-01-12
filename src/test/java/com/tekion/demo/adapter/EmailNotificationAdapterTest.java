package com.tekion.demo.adapter;

import com.tekion.demo.TestDataBuilder;
import com.tekion.demo.notification.Notification;
import com.tekion.demo.notification.NotificationResult;
import com.tekion.demo.notification.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailNotificationAdapterTest {

    private EmailNotificationAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EmailNotificationAdapter();
    }

    @Test
    void shouldSupportEmailNotificationType() {
        assertTrue(adapter.supports(NotificationType.EMAIL));
    }

    @Test
    void shouldNotSupportSmsNotificationType() {
        assertFalse(adapter.supports(NotificationType.SMS));
    }

    @Test
    void shouldSendEmailSuccessfully() {
        Notification notification = TestDataBuilder.createNotificationWithType(NotificationType.EMAIL);
        
        NotificationResult result = adapter.send(notification);
        
        assertTrue(result.isSuccess());
        assertEquals("Email sent successfully", result.getMessage());
    }

    @Test
    void shouldHandleNotificationWithRecipient() {
        Notification notification = Notification.builder()
                .recipient("dealer456")
                .message("Test message")
                .type(NotificationType.EMAIL)
                .build();
        
        NotificationResult result = adapter.send(notification);
        
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldHandleNotificationWithMessage() {
        Notification notification = Notification.builder()
                .recipient("dealer789")
                .message("Important lead notification")
                .type(NotificationType.EMAIL)
                .build();
        
        NotificationResult result = adapter.send(notification);
        
        assertTrue(result.isSuccess());
    }
}

