package com.tekion.demo.router;

import com.tekion.demo.notification.Notification;
import com.tekion.demo.notification.NotificationResult;
import com.tekion.demo.port.NotificationPort;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class NotificationRouter {

    private final List<NotificationPort> adapters;

    private final Map<String, Map<LocalDate, Integer>> rateLimitMap = new HashMap<>();

    private final int MAX_PER_DAY = 3;

    public NotificationRouter(List<NotificationPort> adapters) {
        this.adapters = adapters;
    }

    public NotificationResult send(Notification notification) {
        String leadId = notification.getRecipient();
        LocalDate today = LocalDate.now();
        rateLimitMap.putIfAbsent(leadId, new HashMap<>());
        Map<LocalDate, Integer> dailyMap = rateLimitMap.get(leadId);
        int sentToday = dailyMap.getOrDefault(today, 0);

        if (sentToday >= MAX_PER_DAY) {
            return NotificationResult.builder()
                    .success(false)
                    .message("Rate limit exceeded for lead " + leadId)
                    .build();
        }

        List<NotificationPort> preferredAdapters = new ArrayList<>();
        for (NotificationPort adapter : adapters) {
            if (adapter.supports(notification.getType())) {
                preferredAdapters.add(adapter);
            }
        }

        List<String> errors = new ArrayList<>();
        for (NotificationPort adapter : preferredAdapters) {
            try {
                NotificationResult result = adapter.send(notification);
                if (result.isSuccess()) {
                    dailyMap.put(today, sentToday + 1);
                    return result;
                } else {
                    errors.add(result.getMessage());
                }
            } catch (Exception e) {
                errors.add("Adapter " + adapter.getClass().getSimpleName() + " failed: " + e.getMessage());
            }
        }

        return NotificationResult.builder()
                .success(false)
                .message("All notification attempts failed: " + String.join("; ", errors))
                .build();
    }
}
