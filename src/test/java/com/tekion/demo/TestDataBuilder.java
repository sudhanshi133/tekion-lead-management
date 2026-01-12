package com.tekion.demo;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadSource;
import com.tekion.demo.lead.LeadState;
import com.tekion.demo.lead.valueObject.Email;
import com.tekion.demo.lead.valueObject.PhoneCoordinate;
import com.tekion.demo.lead.valueObject.VehicleInterest;
import com.tekion.demo.notification.Notification;
import com.tekion.demo.notification.NotificationType;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Test data builder for creating test objects
 */
public class TestDataBuilder {

    public static Lead.LeadBuilder defaultLead() {
        return Lead.builder()
                .leadId(UUID.randomUUID().toString())
                .dealerId("dealer123")
                .tenantId("tenant1")
                .siteId("site1")
                .firstName("John")
                .lastName("Doe")
                .email(new Email("john.doe@example.com"))
                .phone(new PhoneCoordinate("+1", "1234567890"))
                .source(LeadSource.WEBSITE)
                .state(LeadState.NEW)
                .vehicleInterest(new VehicleInterest("Toyota", "Camry", 2020, 5000.0))
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now());
    }

    public static Lead createLeadWithSource(LeadSource source) {
        return defaultLead()
                .source(source)
                .build();
    }

    public static Lead createLeadWithVehicleYear(int year) {
        return defaultLead()
                .vehicleInterest(new VehicleInterest("Toyota", "Camry", year, 5000.0))
                .build();
    }

    public static Lead createLeadWithTradeInValue(Double tradeInValue) {
        return defaultLead()
                .vehicleInterest(new VehicleInterest("Toyota", "Camry", 2020, tradeInValue))
                .build();
    }

    public static Lead createLeadWithCreatedAt(ZonedDateTime createdAt) {
        return defaultLead()
                .createdAt(createdAt)
                .build();
    }

    public static Lead createLeadWithState(LeadState state) {
        return defaultLead()
                .state(state)
                .build();
    }

    public static Notification.NotificationBuilder defaultNotification() {
        return Notification.builder()
                .recipient("dealer123")
                .message("Test notification")
                .type(NotificationType.EMAIL);
    }

    public static Notification createNotificationWithType(NotificationType type) {
        return defaultNotification()
                .type(type)
                .build();
    }
}

