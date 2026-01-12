package com.tekion.demo.adapter;

import com.tekion.demo.circuitbreaker.CircuitBreaker;
import com.tekion.demo.circuitbreaker.CircuitBreakerState;
import com.tekion.demo.notification.Notification;
import com.tekion.demo.notification.NotificationResult;
import com.tekion.demo.notification.NotificationType;
import com.tekion.demo.port.NotificationPort;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class EmailNotificationAdapter implements NotificationPort {

    private final CircuitBreaker circuitBreaker;

    public EmailNotificationAdapter() {
        // Circuit breaker: 3 failures threshold, 30 seconds timeout
        this.circuitBreaker = new CircuitBreaker(3, Duration.ofSeconds(30));
    }

    @Override
    public NotificationResult send(Notification notification) {
        // Check circuit breaker state
        if (!circuitBreaker.allowRequest()) {
            System.out.println("EMAIL Circuit Breaker is OPEN - request blocked");
            return NotificationResult.builder()
                    .success(false)
                    .message("Email service unavailable (circuit breaker open)")
                    .build();
        }

        try {
            // Simulate email sending
            System.out.println("Sending EMAIL to: " + notification.getRecipient() +
                    " | Message: " + notification.getMessage());

            // Simulate occasional failures (10% failure rate for demo)
            if (Math.random() < 0.1) {
                throw new RuntimeException("Email service temporarily unavailable");
            }

            circuitBreaker.recordSuccess();
            return NotificationResult.builder()
                    .success(true)
                    .message("Email sent successfully")
                    .build();
        } catch (Exception e) {
            circuitBreaker.recordFailure();
            System.out.println("EMAIL failed: " + e.getMessage() +
                    " (failures: " + circuitBreaker.getFailureCount() + ")");
            return NotificationResult.builder()
                    .success(false)
                    .message("Email failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public boolean supports(NotificationType type) {
        return type == NotificationType.EMAIL;
    }

    public CircuitBreakerState getCircuitBreakerState() {
        return circuitBreaker.getState();
    }

    public void resetCircuitBreaker() {
        circuitBreaker.reset();
    }
}
