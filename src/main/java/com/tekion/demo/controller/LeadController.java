package com.tekion.demo.controller;

import com.tekion.demo.adapter.InMemoryLeadRepository;
import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadSource;
import com.tekion.demo.lead.LeadState;
import com.tekion.demo.lead.valueObject.VehicleInterest;
import com.tekion.demo.router.NotificationRouter;
import com.tekion.demo.scoring.*;
        import com.tekion.demo.adapter.EmailNotificationAdapter;
import com.tekion.demo.adapter.SmsNotificationAdapter;
import com.tekion.demo.notification.Notification;
import com.tekion.demo.notification.NotificationType;
import com.tekion.demo.scoring.rules.*;
import org.springframework.web.bind.annotation.*;

        import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final InMemoryLeadRepository repo = new InMemoryLeadRepository();

    private final NotificationRouter router = new NotificationRouter(List.of(
            new EmailNotificationAdapter(),
            new SmsNotificationAdapter()
    ));

    private final LeadScoringEngine scoringEngine = new LeadScoringEngine(List.of(
            new SourceQualityRule(),
            new VehicleAgeRule(),
            new TradeInValueRule(),
            new EngagementRule(),
            new RecencyRule()
    ));

    // Create a lead
    @PostMapping
    public Map<String, Object> createLead(@RequestBody Lead lead) {

        // 1️⃣ Assign generated ID and timestamps
        lead = Lead.builder()
                .leadId(UUID.randomUUID().toString())
                .dealerId(lead.getDealerId())
                .tenantId(lead.getTenantId())
                .siteId(lead.getSiteId())
                .firstName(lead.getFirstName())
                .lastName(lead.getLastName())
                .source(lead.getSource())
                .state(LeadState.NEW)
                .vehicleInterest(lead.getVehicleInterest())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        // 2️⃣ Save
        repo.save(lead);

        // 3️⃣ Score
        var score = scoringEngine.score(lead);

        // 4️⃣ Notify dealer
        Notification notification = Notification.builder()
                .recipient(lead.getDealerId())
                .message("New lead: " + lead.getFirstName() + " " + lead.getLastName())
                .type(NotificationType.EMAIL)
                .build();

        var notificationResult = router.send(notification);

        return Map.of(
                "lead", lead,
                "score", score,
                "notification", notificationResult
        );
    }

    // Get lead by ID
    @GetMapping("/{dealerId}/{leadId}")
    public Lead getLead(@PathVariable String dealerId, @PathVariable String leadId) {
        return repo.findByIdAndDealerId(leadId, dealerId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
    }

    // List leads by dealer
    @GetMapping("/dealer/{dealerId}")
    public List<Lead> listLeads(@PathVariable String dealerId) {
        return repo.findByDealerIdOrderByScore(dealerId, 100);
    }
}
