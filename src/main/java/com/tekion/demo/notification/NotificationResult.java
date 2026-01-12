package com.tekion.demo.notification;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NotificationResult {
    boolean success;
    String message;
}
