package com.tekion.demo;


import com.tekion.demo.adapter.EmailNotificationAdapter;
import com.tekion.demo.adapter.InMemoryLeadRepository;
import com.tekion.demo.adapter.SmsNotificationAdapter;
import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadSource;
import com.tekion.demo.lead.LeadState;
import com.tekion.demo.lead.valueObject.VehicleInterest;
import com.tekion.demo.router.NotificationRouter;
import com.tekion.demo.scoring.SourceQualityRule;
import com.tekion.demo.scoring.rules.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class TestLeadApp {

    public static void main(String[] args) {

        InMemoryLeadRepository repo = new InMemoryLeadRepository();

        NotificationRouter router = new NotificationRouter(List.of(
                new EmailNotificationAdapter(),
                new SmsNotificationAdapter()
        ));

        LeadScoringEngine engine = new LeadScoringEngine(List.of(
                new SourceQualityRule(),
                new VehicleAgeRule(),
                new TradeInValueRule(),
                new EngagementRule(),
                new RecencyRule()
        ));

        Lead lead = Lead.builder()
                .leadId(UUID.randomUUID().toString())
                .dealerId("dealer123")
                .tenantId("tenantA")
                .siteId("siteX")
                .firstName("John")
                .lastName("Doe")
                .source(LeadSource.REFERRAL)
                .state(LeadState.NEW)
                .vehicleInterest(new VehicleInterest("Toyota", "Camry", 2018, 8000.0))
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        var result = engine.score(lead);
        System.out.println("Lead Score: " + result.getTotalScore());
        System.out.println("Score Breakdown: " + result.getBreakdown());

        repo.save(lead);

        var notificationResult = router.send(
                com.tekion.demo.notification.Notification.builder()
                        .recipient(lead.getDealerId())
                        .message("You have a new lead: " + lead.getFirstName())
                        .type(com.tekion.demo.notification.NotificationType.EMAIL)
                        .build()
        );
        System.out.println("Notification Result: " + notificationResult.getMessage());
    }
}
