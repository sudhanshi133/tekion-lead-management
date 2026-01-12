package com.tekion.demo.router;

import com.tekion.demo.TestDataBuilder;
import com.tekion.demo.adapter.EmailNotificationAdapter;
import com.tekion.demo.adapter.SmsNotificationAdapter;
import com.tekion.demo.notification.Notification;
import com.tekion.demo.notification.NotificationResult;
import com.tekion.demo.notification.NotificationType;
import com.tekion.demo.port.NotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationRouterTest {

    private NotificationRouter router;
    private EmailNotificationAdapter emailAdapter;
    private SmsNotificationAdapter smsAdapter;

    @BeforeEach
    void setUp() {
        emailAdapter = new EmailNotificationAdapter();
        smsAdapter = new SmsNotificationAdapter();
        router = new NotificationRouter(Arrays.asList(emailAdapter, smsAdapter));
    }

    @Test
    void shouldSendEmailNotificationSuccessfully() {
        Notification notification = TestDataBuilder.createNotificationWithType(NotificationType.EMAIL);
        
        NotificationResult result = router.send(notification);
        
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldSendSmsNotificationSuccessfully() {
        Notification notification = TestDataBuilder.createNotificationWithType(NotificationType.SMS);
        
        NotificationResult result = router.send(notification);
        
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldEnforceRateLimitPerDay() {
        Notification notification = Notification.builder()
                .recipient("dealer123")
                .message("Test notification")
                .type(NotificationType.EMAIL)
                .build();
        
        // Send 3 notifications (max allowed)
        NotificationResult result1 = router.send(notification);
        NotificationResult result2 = router.send(notification);
        NotificationResult result3 = router.send(notification);
        
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());
        
        // 4th notification should be rate limited
        NotificationResult result4 = router.send(notification);
        
        assertFalse(result4.isSuccess());
        assertTrue(result4.getMessage().contains("Rate limit exceeded"));
    }

    @Test
    void shouldTrackRateLimitPerRecipient() {
        Notification notification1 = Notification.builder()
                .recipient("dealer123")
                .message("Test")
                .type(NotificationType.EMAIL)
                .build();
        
        Notification notification2 = Notification.builder()
                .recipient("dealer456")
                .message("Test")
                .type(NotificationType.EMAIL)
                .build();
        
        // Send 3 to dealer123
        router.send(notification1);
        router.send(notification1);
        router.send(notification1);
        
        // dealer456 should still be able to receive
        NotificationResult result = router.send(notification2);
        
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldSelectCorrectAdapterForNotificationType() {
        NotificationPort mockEmailAdapter = mock(NotificationPort.class);
        NotificationPort mockSmsAdapter = mock(NotificationPort.class);
        
        when(mockEmailAdapter.supports(NotificationType.EMAIL)).thenReturn(true);
        when(mockEmailAdapter.supports(NotificationType.SMS)).thenReturn(false);
        when(mockSmsAdapter.supports(NotificationType.EMAIL)).thenReturn(false);
        when(mockSmsAdapter.supports(NotificationType.SMS)).thenReturn(true);
        
        when(mockEmailAdapter.send(any())).thenReturn(
                NotificationResult.builder().success(true).message("Email sent").build());
        when(mockSmsAdapter.send(any())).thenReturn(
                NotificationResult.builder().success(true).message("SMS sent").build());
        
        NotificationRouter testRouter = new NotificationRouter(Arrays.asList(mockEmailAdapter, mockSmsAdapter));
        
        Notification emailNotification = TestDataBuilder.createNotificationWithType(NotificationType.EMAIL);
        testRouter.send(emailNotification);
        
        verify(mockEmailAdapter, times(1)).send(any());
        verify(mockSmsAdapter, never()).send(any());
    }

    @Test
    void shouldHandleAdapterFailureAndReturnError() {
        NotificationPort failingAdapter = mock(NotificationPort.class);
        when(failingAdapter.supports(NotificationType.EMAIL)).thenReturn(true);
        when(failingAdapter.send(any())).thenReturn(
                NotificationResult.builder().success(false).message("Adapter failed").build());
        
        NotificationRouter testRouter = new NotificationRouter(List.of(failingAdapter));
        
        Notification notification = TestDataBuilder.createNotificationWithType(NotificationType.EMAIL);
        NotificationResult result = testRouter.send(notification);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("failed"));
    }

    @Test
    void shouldHandleAdapterException() {
        NotificationPort throwingAdapter = mock(NotificationPort.class);
        when(throwingAdapter.supports(NotificationType.EMAIL)).thenReturn(true);
        when(throwingAdapter.send(any())).thenThrow(new RuntimeException("Connection error"));
        
        NotificationRouter testRouter = new NotificationRouter(List.of(throwingAdapter));
        
        Notification notification = TestDataBuilder.createNotificationWithType(NotificationType.EMAIL);
        NotificationResult result = testRouter.send(notification);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("failed"));
    }
}

