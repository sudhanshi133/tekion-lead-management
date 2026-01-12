package com.tekion.demo.notification;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Notification {
    String recipient;
    String message;
    NotificationType type;
}